package org.solmix.runtime.io;



import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class DelegatingInputStream extends FilterInputStream {
    protected boolean cached;
    
    public DelegatingInputStream(InputStream is) {
        super(is);
    }


    public void setInputStream(InputStream inputStream) {
        in = inputStream;
    }
    public InputStream getInputStream() {
        return in;
    }
    
    /**
     * Read the entire original input stream and cache it.  Useful
     * if switching threads or doing something where the original
     * stream may not be valid by the time the next read() occurs
     */
    public void cacheInput() {
        if (!cached) {
            CachedOutputStream cache = new CachedOutputStream();
            try {
                InputStream origIn = in;
                IOUtils.copy(in, cache);
                if (cache.size() > 0) {
                    in = cache.getInputStream();
                } else {
                    in = new ByteArrayInputStream(new byte[0]);
                }
                cache.close();
                origIn.close();
            } catch (IOException e) {
                //ignore
            }
            cached = true;
        }
    }
    
}