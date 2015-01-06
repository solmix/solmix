/**
 * Copyright 2014 The Solmix Project
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

package org.solmix.runtime.exchange.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.Processor;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月17日
 */

public abstract class AbstractPipeline implements Pipeline {

    protected Processor processor;
    protected String address;
    
    public AbstractPipeline(String address) {
        this.address = address;
    }

    @Override
    public void close(Message message) throws IOException {
        OutputStream os = message.getContent(OutputStream.class);
        if (os != null) {
            os.close();
        }
        InputStream in = message.getContent(InputStream.class);
        if (in != null) {
            in.close();
        }
    }
    
    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public void setProcessor(Processor processor) {
        if (this.processor != processor) {
            Processor old = this.processor;
            if (processor != null) {
                this.processor = processor;
                if (getLogger().isTraceEnabled()) {
                    getLogger().trace(
                        "Register message Processor: " + processor);
                }
                if (old == null) {
                    try {
                        activate(processor);
                    } catch (RuntimeException ex) {
                        this.processor = null;
                        throw ex;
                    }
                } else {
                    if (old != null) {
                        getLogger().trace(
                            "unregistering incoming observer: " + old);
                        deactivate(old);
                    }
                    this.processor = processor;
                }
            }
        }
    }

    protected abstract Logger getLogger();

    protected void activate(Processor p) {

    }

    protected void deactivate(Processor p) {

    }

    @Override
    public void close() {
        // nothing todo
    }

    @Override
    public String toString() {
        return "pipeline: " + this.getClass() + System.identityHashCode(this)
            + "address: " + getAddress();
    }
}
