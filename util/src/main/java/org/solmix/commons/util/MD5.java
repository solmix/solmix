package org.solmix.commons.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5 {

	private static final Logger LOG  = LoggerFactory.getLogger(MD5.class);
	
	private MessageDigest md;

	public MD5() {
		try {
			this.md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Could not find MD5 Algorithm");
		}
	}

	public void add(InputStream is) throws IOException {

		byte[] bytes = new byte[1024];
		int len;
		try {
			while ((len = is.read(bytes, 0, bytes.length)) != -1) {
				md.update(bytes, 0, len);
			}
		} catch (IOException e) {
			throw new IOException("Couldn't read data stream: "
					+ e.getMessage());
		}
	}

	public void add(String input) {
		add(input.getBytes());
	}

	public void add(byte[] input) {
		this.md.update(input);
	}

	public byte[] getDigest() throws IOException {

		return this.md.digest();
	}

	public String getDigestString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int hi = (bytes[i] >> 4) & 0xf;
			int lo = bytes[i] & 0xf;
			sb.append(Character.forDigit(hi, 16));
			sb.append(Character.forDigit(lo, 16));
		}
		return sb.toString();
	}

	public String getDigestString() throws IOException {
		return getDigestString(getDigest());
	}

	public static byte[] getDigest(String input) throws IOException {
		MD5 md5 = new MD5();
		md5.add(input);
		return md5.getDigest();
	}

	public static String getEncodedDigest(String input) throws IOException {

		return Base64Utils.encode(getDigest(input));
	}

	public static String getDigestString(InputStream is) throws IOException {

		MD5 md5 = new MD5();
		md5.add(is);
		return md5.getDigestString();
	}


	public static String getMD5Checksum(String buf) {
		ByteArrayInputStream stream = new ByteArrayInputStream(buf.getBytes());
		return getMD5Checksum(stream);
	}

	public static String getMD5Checksum(File file) {
		try {
			InputStream fin = new FileInputStream(file);
			return getMD5Checksum(fin);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	private static String getMD5Checksum(InputStream fin) {
		try {
			final MessageDigest md5er = MessageDigest.getInstance("MD5");
			final byte[] buffer = new byte[1024];
			int read;
			do {
				read = fin.read(buffer);
				if (read > 0) {
					md5er.update(buffer, 0, read);
				}
			} while (read != -1);
			fin.close();
			final byte[] digest = md5er.digest();
			if (digest == null) {
				return null;
			}
			final StringBuilder strDigest = new StringBuilder();
			for (final Byte b : digest) {
				strDigest.append(Integer.toString((b & 0xff) + 0x100, 16)
						.substring(1).toLowerCase());
			}
			return strDigest.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
}
