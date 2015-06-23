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

package org.solmix.exchange.model;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月26日
 */

public class FaultInfo extends AbstractMessageInfo {

    private NamedID faultName;

    FaultInfo(OperationInfo op, NamedID messageId, NamedID faultId) {
        super(op, messageId);
        this.faultName = faultId;
    }

    public NamedID getFaultID() {
        return faultName;
    }

    public void setFaultID(NamedID fid) {
        faultName = fid;
    }

    @Override
    public int hashCode() {
        return faultName == null ? -1 : faultName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof FaultInfo)) {
            return false;
        }
        FaultInfo oi = (FaultInfo) o;
        return equals(faultName, oi.faultName) && super.equals(o);
    }
}
