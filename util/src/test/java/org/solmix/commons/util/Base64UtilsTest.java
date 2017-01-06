package org.solmix.commons.util;

import org.junit.Test;

public class Base64UtilsTest {

	@Test
	public void test() throws Base64Exception {
		String code="12345678901234567890123456789011";
		String entr = Base64Utils.encode(code.getBytes());
		System.out.println(entr);
		System.out.println(entr.length());
		byte[] de = Base64Utils.decode(entr);
		System.out.println(new String(de));
	}

}
