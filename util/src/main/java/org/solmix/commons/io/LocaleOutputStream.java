package org.solmix.commons.io;

import java.io.IOException;
import java.io.OutputStream;

public class LocaleOutputStream extends OutputStream {

	private final String language;
	 public LocaleOutputStream(OutputStream one,String language)
	    {
	        this.one = null;
	        this.one = one;
	        this.language=language;
	    }

	    public String getLanguage() {
		return language;
	}

		public void write(byte buffer[], int offset, int length)
	        throws IOException
	    {
	        one.write(buffer, offset, length);
	    }

	    public void write(int character)
	        throws IOException
	    {
	        one.write(character);
	    }

	    public void close()
	        throws IOException
	    {
	        one.close();
	    }

	    public void flush()
	        throws IOException
	    {
	        one.flush();
	    }

	    private OutputStream one;
}
