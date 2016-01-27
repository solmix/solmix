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
        
        String text ="2015-01-01~2015-12-11:2/192.168.149.1-192.168.149.9:2";
        NetUtils.getLocalAddress().getHostAddress();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(text.getBytes(), "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIL1Vub2R41nA2nxjk414023rooV2KDulI0IX12+/cU5RJoCkln6uB5B8xSh6NGWo22Fl5IfcAflL+g8FV5FgglL1tXWXMbiDYD1zvs2Xrf77hWV0Xn6InNS3Ijrfxw1AggUc1mXehY5/Le9CPGszRkQmWeh6VtApG3UM/EkdIh9AgMBAAECgYEAgGmggDNIsFAt2cniV7ChpciSXpbTZ+LqSWzHTr6ESstACKCy74ZY2lqiyD2HdVT4BeH0YXVwPl2u31NjRKB1w8zcyAKvYXrXKGp7OVByz/YMVcIijtui/8MLrRiAxe69OiDRhE9lA7io+gnYUKLnyv7HJDjQzIvwz/1qJS9iqKUCQQDK7Rvu4ZC54YwAYnVRnyh6geJ1DaGYd09BMIAlrR4Poc9f1Ej+RJiJX3wlHHzveLC7TSHF/D3B3WIxelp3NvovAkEApTWif6Bi2S+MkKvgIMf02H3eLd+2+VmK6QV0SUTFdwH9QPEv1Z+WJpMckM+8cavAxYAUemo0J7fEvPLa16y5EwJBAJA0JyF+kcZGDaNIVG6IV8+W9UKRSUB7qIp+2NHtT+tz5VYIGUb3oB4fCK2mrPHQJmczzMhRE+HsXJckh50oKGkCQF9RlrP0IZQVbxB5WhMPyyCtXmcxUCyFkTPoxbMQTq3fI/M4NNUYAlW3Qx/5+0vKQKqyvx3x8K2JxaUOqV0OdqkCQALxlO+Sls96MOo/OaBsCRLL0yQf49+Kcc7RhSSem5rmhL3lXgmpHMFsg5dxB9SSdPVy/+hpHigr59Uk0+7Vrc4=");
        System.out.println( Base64Utils.encode(encodedData));
    }

}