package org.solmix.atmosphere.client;


public interface AtmosphereTransportFailureHandler {
    public void onTransportFailure(String errorMsg, AtmosphereRequest request);
}
