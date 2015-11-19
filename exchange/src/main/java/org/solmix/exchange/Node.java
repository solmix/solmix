package org.solmix.exchange;

public interface Node
{
    
    String getAddress();
    
    boolean isAvailable();
    
    void destroy();

}
