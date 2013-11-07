package org.solmix.atmosphere.client;


public interface AtmosphereMessagePublishedHandler {
    public void onMessagePublished(AtmosphereRequestConfig request, AtmosphereResponse response);
}
