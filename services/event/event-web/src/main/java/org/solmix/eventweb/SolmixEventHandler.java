/*
 * SOLMIX PROJECT
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.eventweb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Serializer;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.api.serialize.JSParser;
import org.solmix.commons.util.IOUtil;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.serialize.JSParserFactoryImpl;

/**
 * This is a bridge between Solmix Event service and Atmosphere framework.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-9-30
 */
public class SolmixEventHandler extends AbstractReflectorAtmosphereHandler implements AtmosphereHandler
{

    static final Logger logger = LoggerFactory.getLogger("AtmosphereHandler");

    private String eventTopics;

    private JSParser jsonParser;

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.cpr.AtmosphereHandler#onRequest(org.atmosphere.cpr.AtmosphereResource)
     */
    @Override
    public void onRequest(AtmosphereResource r) throws IOException {
        AtmosphereRequest request = r.getRequest();
        String httpMethod = request.getMethod();
        if (httpMethod.equalsIgnoreCase("GET")) {
            doGet(r);
        } else if (httpMethod.equalsIgnoreCase("POST")) {
            doPost(r);
        }

    }

    /**
     * @param r
     */
    protected void doGet(final AtmosphereResource r) {
        r.getResponse().setCharacterEncoding(r.getRequest().getCharacterEncoding());
        r.getResponse().setContentType("application/json");
        r.setSerializer(new Serializer() {

            Charset charset = Charset.forName(r.getResponse().getCharacterEncoding());

            @Override
            public void write(OutputStream os, Object o) throws IOException {
                try {
                    if (logger.isDebugEnabled())
                        logger.debug("Writing object to JSON outputstream with charset: " + charset.displayName());
                    StringWriter out = new StringWriter();
                    getJSParser().toJSON(out, o);
                    os.write(out.toString().getBytes(charset));
                    os.flush();
                } catch (SlxException e) {
                    throw new IOException("Failed to serialize object to JSON", e);
                }
            }
        });
        r.suspend();

    }

    /**
     * @return
     */
    protected JSParser getJSParser() {
        if (jsonParser == null) {
            jsonParser = JSParserFactoryImpl.getInstance().get();
        }
        return jsonParser;
    }

    /**
     * Deliver the event to Solmix Event-Manager service.
     * 
     * @param r
     */
    @SuppressWarnings("unchecked")
    protected void doPost(AtmosphereResource r) {

        StringWriter out = new StringWriter();
        try {
            IOUtil.copyCharacterStreams(r.getRequest().getReader(), out);
        } catch (IOException e) {
            logger.error("Can't read data from request", e);
        }
        String queryStr = out.toString();
        DelegateClientEvent event = null;
        try {
            Map<String, Object> data = getJSParser().toJavaObject(queryStr, Map.class);
            if (data != null && data.size() != 0) {
                event = new DelegateClientEvent(data);
                event.setBroadcasterID(r.getBroadcaster().getID());
                SlxContext.getEventManager().postEvent(event);
            }
        } catch (SlxException e) {
            logger.error("Can't parser string to JavaObject", e);
        }

    }


    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.cpr.AtmosphereHandler#destroy()
     */
    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /**
     * @return the eventTopic
     */
    public String getEventTopics() {
        return eventTopics;
    }

    /**
     * @param eventTopic the eventTopic to set
     */
    public void setEventTopic(String eventTopics) {
        this.eventTopics = eventTopics;
    }

}
