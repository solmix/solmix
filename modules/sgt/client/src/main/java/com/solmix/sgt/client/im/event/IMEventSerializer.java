package com.solmix.sgt.client.im.event;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;
import org.atmosphere.gwt.shared.SerialMode;


@SerialTypes(value={IMEvent.class},mode=SerialMode.JSON,pushMode=SerialMode.JSON)
public abstract class IMEventSerializer extends AtmosphereGWTSerializer {

}
