/**
 * Copyright (c) 2014 The Solmix Project
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Pipeline;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月14日
 */

public class UpPipelineSelector extends AbstractPipelineSelector {

    private static final Logger LOG = LoggerFactory.getLogger(DeferredPipelineSelector.class);

    public UpPipelineSelector() {
        super();
    }

    public UpPipelineSelector(Pipeline pipeline) {
        super(pipeline);
    }
    
    @Override
    public void prepare(Message message) {
        Pipeline pl = message.get(Pipeline.class);
        if (pl != null) {
            pl = getSelectedPipeline(message);
            message.put(Pipeline.class, pl);
        }
    }

   
    @Override
    public Pipeline select(Message message) {
        Pipeline pl = message.get(Pipeline.class);
        if (pl != null) {
            pl = getSelectedPipeline(message);
            message.put(Pipeline.class, pl);
        }
        return pl;
    }

    
    @Override
    protected Logger getLogger() {
        return LOG;
    }

}
