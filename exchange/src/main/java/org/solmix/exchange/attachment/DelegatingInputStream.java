package org.solmix.exchange.attachment;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.solmix.runtime.io.IOUtils;

public class DelegatingInputStream extends InputStream  {
    private InputStream is;
    private AttachmentDeserializer deserializer;
    private boolean isClosed;

    /**
     * @param source
     */
    DelegatingInputStream(InputStream is, AttachmentDeserializer ads) {
        this.is = is;
        deserializer = ads;
    }
    DelegatingInputStream(InputStream is) {
        this.is = is;
        deserializer = null;
    }

    @Override
    public void close() throws IOException {
        IOUtils.consume(is);
        is.close();
        if (!isClosed && deserializer != null) {
            deserializer.markClosed(this);
        }
        isClosed = true;
    }

    public void transferTo(File destinationFile) throws IOException {
        if (isClosed) {
            throw new IOException("Stream is closed");
        }
        IOUtils.transferTo(is, destinationFile);
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        this.isClosed = closed;
    }

    public int read() throws IOException {
        return this.is.read();
    }

    @Override
    public int available() throws IOException {
        return this.is.available();
    }

    @Override
    public synchronized void mark(int arg0) {
        this.is.mark(arg0);
    }

    @Override
    public boolean markSupported() {
        return this.is.markSupported();
    }

    @Override
    public int read(byte[] bytes, int arg1, int arg2) throws IOException {
        return this.is.read(bytes, arg1, arg2);
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return this.is.read(bytes);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.is.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.is.skip(n);
    }

    public void setInputStream(InputStream inputStream) {
        this.is = inputStream;
    }

    
    public InputStream getInputStream() {
        return is;
    }
}