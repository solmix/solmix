package org.solmix.exchange.attachment;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.solmix.exchange.Message;
import org.solmix.runtime.io.CacheSizeExceededException;
import org.solmix.runtime.io.CachedOutputStream;
import org.solmix.runtime.io.DelegatingInputStream;
import org.solmix.runtime.io.IOUtils;

public class AttachmentDataSource implements DataSource {

    private final String ct;    
    private CachedOutputStream cache;
    private InputStream ins;
    private DelegatingInputStream delegate;
    private String name;
    
    public AttachmentDataSource(String ctParam, InputStream inParam) throws IOException {
        this.ct = ctParam;        
        ins = inParam;
    }

    public boolean isCached() {
        return cache != null;
    }
    public void cache(Message message) throws IOException {
        if (cache == null) {
            cache = new CachedOutputStream();
            AttachmentUtils.setStreamedAttachmentProperties(message, cache);
            try {
                IOUtils.copy(ins, cache);
                cache.lockOutputStream();
                if (delegate != null) {
                    delegate.setInputStream(cache.getInputStream());
                }
            } catch (CacheSizeExceededException cee) {
                cache.close();
                cache = null;
                throw cee;
            } catch (IOException cee) {
                cache.close();
                cache = null;
                throw cee;
            } finally {
                try {
                    ins.close();                
                } catch (Exception ex) {
                    //ignore
                }
                ins = null;
            }
        }
    }
    public void hold(Message message) throws IOException {
        cache(message);
        cache.holdTempFile();
    }
    public void release() {
        if (cache != null) {
            cache.releaseTempFileHold();
        }
    }
    
    public String getContentType() {
        return ct;
    }

    public InputStream getInputStream() {
        try {
            if (cache != null) {
                return cache.getInputStream();
            }
            if (delegate == null) {
                delegate = new DelegatingInputStream(ins);
            }
            return delegate;
        } catch (IOException e) {
            return null;
        }
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}