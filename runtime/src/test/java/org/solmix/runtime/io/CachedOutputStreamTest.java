package org.solmix.runtime.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CachedOutputStreamTest extends CachedStreamTestBase {
    
    @Override
    protected void reloadDefaultProperties() {
        CachedOutputStream.setDefaultThreshold(-1);
        CachedOutputStream.setDefaultMaxSize(-1);
        CachedOutputStream.setDefaultCipherTransformation(null);
    }
    
    @Override
    protected Object createCache() {
        return new CachedOutputStream();
    }
    
    @Override
    protected Object createCache(long threshold) {
        return createCache(threshold, null);
    }
    
    @Override
    protected Object createCache(long threshold, String transformation) {
        CachedOutputStream cos = new CachedOutputStream();
        cos.setThreshold(threshold);
        cos.setCipherTransformation(transformation);
        return cos;
    }
    
    @Override
    protected String getResetOutValue(String result, Object cache) throws IOException {
        CachedOutputStream cos = (CachedOutputStream)cache;
        cos.write(result.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        cos.resetOut(out, true);
        return out.toString();        
    }
    
    @Override
    protected File getTmpFile(String result, Object cache) throws IOException {
        CachedOutputStream cos = (CachedOutputStream)cache;
        cos.write(result.getBytes("utf-8"));
        cos.flush();
        return cos.getTempFile();
    }

    @Override
    protected Object getInputStreamObject(Object cache) throws IOException {
        return ((CachedOutputStream)cache).getInputStream();
    }
    
    @Override
    protected String readFromStreamObject(Object obj) throws IOException {
        return readFromStream((InputStream)obj);
    }

    @Override
    protected String readPartiallyFromStreamObject(Object cache, int len) throws IOException {
        return readPartiallyFromStream((InputStream)cache, len);
    }
}
    
   
