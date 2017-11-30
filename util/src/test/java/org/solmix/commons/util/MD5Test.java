package org.solmix.commons.util;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MD5Test
{

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws IOException {
       String md5 = MD5.getEncodedDigest("aaaadddd");
       org.junit.Assert.assertEquals("pzTHm3ukxPNTeO3buZHH6A==", md5);
    }

}
