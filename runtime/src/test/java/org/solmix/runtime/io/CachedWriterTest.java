package org.solmix.runtime.io;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public class CachedWriterTest extends CachedStreamTestBase {
    @Override
    protected void reloadDefaultProperties() {
        CachedWriter.setDefaultThreshold(-1);
        CachedWriter.setDefaultMaxSize(-1);
        CachedWriter.setDefaultCipherTransformation(null);
    }

    @Override
    protected Object createCache() {
        return new CachedWriter();
    }
    
    @Override
    protected Object createCache(long threshold) {
        return createCache(threshold, null);
    }
    
    @Override
    protected Object createCache(long threshold, String transformation) {
        CachedWriter cos = new CachedWriter();
        cos.setThreshold(threshold);
        cos.setCipherTransformation(transformation);
        return cos;
    }
    
    @Override
    protected String getResetOutValue(String result, Object cache) throws IOException {
        CachedWriter cos = (CachedWriter)cache;
        cos.write(result);
        StringWriter out = new StringWriter();
        cos.resetOut(out, true);
        return out.toString();
    }
    
    @Override
    protected File getTmpFile(String result, Object cache) throws IOException {
        CachedWriter cos = (CachedWriter)cache;
        cos.write(result);
        cos.flush();
        return cos.getTempFile();
    }

    @Override
    protected Object getInputStreamObject(Object cache) throws IOException {
        return ((CachedWriter)cache).getReader();
    }

    @Override
    protected String readFromStreamObject(Object obj) throws IOException {
        return readFromReader((Reader)obj);
    }

    @Override
    protected String readPartiallyFromStreamObject(Object cache, int len) throws IOException {
        return readPartiallyFromReader((Reader)cache, len);
    }
}
    
   
