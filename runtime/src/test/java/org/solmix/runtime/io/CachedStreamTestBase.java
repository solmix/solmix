package org.solmix.runtime.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;

public abstract class CachedStreamTestBase extends Assert {
    // use two typical ciphers for testing
    private static final String[] CIPHER_LIST = {"RC4", "AES/CTR/NoPadding"};

    protected abstract void reloadDefaultProperties();
    protected abstract Object createCache();
    protected abstract Object createCache(long threshold);
    protected abstract Object createCache(long threshold, String transformation);
    protected abstract String getResetOutValue(String result, Object cache) throws IOException;
    protected abstract File getTmpFile(String result, Object cache) throws IOException;
    protected abstract Object getInputStreamObject(Object cache) throws IOException;
    protected abstract String readFromStreamObject(Object cache) throws IOException;
    protected abstract String readPartiallyFromStreamObject(Object cache, int len) throws IOException;
    
    @Test
    public void testResetOut() throws IOException {
        String result = initTestData(16);
        Object cache = createCache();
        String test = getResetOutValue(result, cache);
        assertEquals("The test stream content isn't same ", test , result);
        close(cache);
    }
    
    @Test
    public void testDeleteTmpFile() throws IOException {
        Object cache = createCache();
        //ensure output data size larger then 64k which will generate tmp file
        String result = initTestData(129);
        File tempFile = getTmpFile(result, cache);
        assertNotNull(tempFile);
        //assert tmp file is generated
        assertTrue(tempFile.exists());
        close(cache);
        //assert tmp file is deleted after close the CachedOutputStream
        assertFalse(tempFile.exists());
    }

    @Test
    public void testDeleteTmpFile2() throws IOException {
        Object cache = createCache();
        //ensure output data size larger then 128k which will generate tmp file
        String result = initTestData(130);
        File tempFile = getTmpFile(result, cache);
        assertNotNull(tempFile);
        //assert tmp file is generated
        assertTrue(tempFile.exists());
        Object in = getInputStreamObject(cache);
        close(cache);
        //assert tmp file is not deleted when the input stream is open
        assertTrue(tempFile.exists());
        close(in);
        //assert tmp file is deleted after the input stream is closed
        assertFalse(tempFile.exists());
    }
    
    @Test
    public void testEncryptAndDecryptWithDeleteOnClose() throws IOException {
        // need a 8-bit cipher so that all bytes are flushed when the stream is flushed.
        for (String cipher: CIPHER_LIST) {
            verifyEncryptAndDecryptWithDeleteOnClose(cipher);
        }
    }
    
    private void verifyEncryptAndDecryptWithDeleteOnClose(String cipher) throws IOException {
        Object cache = createCache(4, cipher);
        final String text = "Hello Secret World!";
        File tmpfile = getTmpFile(text, cache);
        assertNotNull(tmpfile);

        final String enctext = readFromStream(new FileInputStream(tmpfile));
        assertFalse("text is not encoded", text.equals(enctext));

        Object fin = getInputStreamObject(cache);

        assertTrue("file is deleted", tmpfile.exists());
        
        final String dectext = readFromStreamObject(fin);
        assertEquals("text is not decoded correctly", text, dectext);

        // the file is deleted when cos is closed while all the associated inputs are closed
        assertTrue("file is deleted", tmpfile.exists());
        close(cache);
        assertFalse("file is not deleted", tmpfile.exists());
    }

    @Test
    public void testEncryptAndDecryptWithDeleteOnInClose() throws IOException {
        for (String cipher: CIPHER_LIST) {
            verifyEncryptAndDecryptWithDeleteOnInClose(cipher);
        }
    }

    private void verifyEncryptAndDecryptWithDeleteOnInClose(String cipher) throws IOException {
        // need a 8-bit cipher so that all bytes are flushed when the stream is flushed.
        Object cache = createCache(4, cipher);
        final String text = "Hello Secret World!";
        File tmpfile = getTmpFile(text, cache);
        assertNotNull(tmpfile);
        
        final String enctext = readFromStream(new FileInputStream(tmpfile));
        assertFalse("text is not encoded", text.equals(enctext));

        Object fin = getInputStreamObject(cache);

        close(cache);
        assertTrue("file is deleted", tmpfile.exists());
        
        // the file is deleted when cos is closed while all the associated inputs are closed
        final String dectext = readFromStreamObject(fin);
        assertEquals("text is not decoded correctly", text, dectext);
        assertFalse("file is not deleted", tmpfile.exists());
    }

    @Test
    public void testEncryptAndDecryptPartially() throws IOException {
        for (String cipher: CIPHER_LIST) {
            verifyEncryptAndDecryptPartially(cipher);
        }
    }

    private void verifyEncryptAndDecryptPartially(String cipher) throws IOException {
        // need a 8-bit cipher so that all bytes are flushed when the stream is flushed.
        Object cache = createCache(4, cipher);
        final String text = "Hello Secret World!";
        File tmpfile = getTmpFile(text, cache);
        assertNotNull(tmpfile);

        Object fin = getInputStreamObject(cache);
        // read partially and keep the stream open
        String pdectext = readPartiallyFromStreamObject(fin, 4);
        assertTrue("text is not decoded correctly", text.startsWith(pdectext));

        Object fin2 = getInputStreamObject(cache);

        final String dectext = readFromStreamObject(fin2);
        assertEquals("text is not decoded correctly", text, dectext);

        // close the partially read stream
        if (fin instanceof InputStream) {
            ((InputStream)fin).close();
        } else if (fin instanceof Reader) {
            ((Reader)fin).close();
        }

        // the file is deleted when cos is closed while all the associated inputs are closed
        assertTrue("file is deleted", tmpfile.exists());
        close(cache);
        assertFalse("file is not deleted", tmpfile.exists());
    }


    @Test
    public void testUseSysProps() throws Exception {
        String old = System.getProperty("org.solmix.io.CachedOutputStream.Threshold");
        try {
            System.clearProperty("org.solmix.io.CachedOutputStream.Threshold");
            reloadDefaultProperties();
            Object cache = createCache();
            File tmpfile = getTmpFile("Hello World!", cache);
            assertNull("expects no tmp file", tmpfile);
            close(cache);
            
            System.setProperty("org.solmix.io.CachedOutputStream.Threshold", "4");
            reloadDefaultProperties();
            cache = createCache();
            tmpfile = getTmpFile("Hello World!", cache);
            assertNotNull("expects a tmp file", tmpfile);
            assertTrue("expects a tmp file", tmpfile.exists());
            close(cache);
            assertFalse("expects no tmp file", tmpfile.exists());
        } finally {
            if (old != null) {
                System.setProperty("org.solmix.io.CachedOutputStream.Threshold", old);
            }
        }
    }


    @Test
    public void testUseContainerProps() throws Exception {
        Container oldbus = ContainerFactory.getThreadDefaultContainer(false); 
        try {
            Object cache = createCache(64);
            File tmpfile = getTmpFile("Hello World!", cache);
            assertNull("expects no tmp file", tmpfile);
            close(cache);
            
            IMocksControl control = EasyMock.createControl();
            
            Container b = control.createMock(Container.class);
            EasyMock.expect(b.getProperty("container.io.CachedOutputStream.Threshold")).andReturn("4");
            EasyMock.expect(b.getProperty("container.io.CachedOutputStream.MaxSize")).andReturn(null);
            EasyMock.expect(b.getProperty("container.io.CachedOutputStream.CipherTransformation")).andReturn(null);
        
            ContainerFactory.setThreadDefaultContainer(b);
            
            control.replay();

            cache = createCache();
            tmpfile = getTmpFile("Hello World!", cache);
            assertNotNull("expects a tmp file", tmpfile);
            assertTrue("expects a tmp file", tmpfile.exists());
            close(cache);
            assertFalse("expects no tmp file", tmpfile.exists());
            
            control.verify();
        } finally {
            ContainerFactory.setThreadDefaultContainer(oldbus);
        }
    }
    
    private static void close(Object obj) throws IOException {
        if (obj instanceof CachedOutputStream) {
            ((CachedOutputStream)obj).close();
        } else if (obj instanceof CachedWriter) {
            ((CachedWriter)obj).close();
        } else if (obj instanceof InputStream) {
            ((InputStream)obj).close();
        } else if (obj instanceof Reader) {
            ((Reader)obj).close();
        }
    }

    protected static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            byte[] b = new byte[100];
            for (;;) {
                int n = is.read(b, 0, b.length);
                if (n < 0) {
                    break;
                }
                buf.write(b, 0, n);
            }
        } finally {
            is.close();
        }
        return new String(buf.toByteArray(), "UTF-8");
    }

    protected static String readPartiallyFromStream(InputStream is, int len) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] b = new byte[len];
        int rn = 0;
        for (;;) {
            int n = is.read(b, 0, b.length);
            if (n < 0) {
                break;
            }
            buf.write(b, 0, n);
            rn += n;
            if (len <= rn) {
                break;
            }
        }
        return new String(buf.toByteArray(), "UTF-8");
    }
 
    protected static String readFromReader(Reader is) throws IOException {
        StringBuilder buf = new StringBuilder();
        try {
            char[] b = new char[100];
            for (;;) {
                int n = is.read(b, 0, b.length);
                if (n < 0) {
                    break;
                }
                buf.append(b, 0, n);
            }
        } finally {
            is.close();
        }
        return buf.toString();
    }
    
    protected static String readPartiallyFromReader(Reader is, int len) throws IOException {
        StringBuilder buf = new StringBuilder();
        char[] b = new char[len];
        int rn = 0;
        for (;;) {
            int n = is.read(b, 0, b.length);
            if (n < 0) {
                break;
            }
            buf.append(b, 0, n);
            rn += n;
            if (len <= rn) {
                break;
            }
        }
        return buf.toString();
    }
    
    private static String initTestData(int packetSize) {
        String temp = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+?><[]/0123456789";
        String result = new String();
        for (int i = 0; i <  1024 * packetSize / temp.length(); i++) {
            result = result + temp;
        }
        return result;
    }
}
    
   
