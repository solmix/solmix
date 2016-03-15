package org.solmix.runtime.helper;

import org.solmix.commons.util.Base64Utils;
import org.solmix.commons.util.NetUtils;
import org.solmix.commons.util.RSAUtils;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年10月26日
 */

public class Generation
{

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        
        String text ="2015-01-01~2016-10-11:1/10.176.34.59";
        NetUtils.getLocalAddress().getHostAddress();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(text.getBytes(), "");
        System.out.println( Base64Utils.encode(encodedData));
    }

}