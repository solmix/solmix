package org.solmix.commons.util;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.commons.io.Bytes;

public class BytesTest {

	
	@Test
	public void hexString() {
		String target="1FC593ED86C311BABC7F31E46107ADA2";
		byte[] bytes = Bytes.hexStringToByte(target);
		String strs = Bytes.bytesToHexString(bytes);
		Assert.assertEquals(target, strs.replace(" ", ""));
		
	}
}
