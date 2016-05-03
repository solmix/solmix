
package org.solmix.commons.util;

import java.util.Random;
import java.util.UUID;

public class TokenUtils
{

    static final char[] hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    static final char[] digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    static final Random rand = new Random();

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public static String randomString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int loop = 0; loop < length; ++loop) {
            sb.append(hexDigits[rand.nextInt(hexDigits.length)]);
        }
        return sb.toString();
    }

    public static String randomNumber(int length) {
        StringBuffer sb = new StringBuffer();
        for (int loop = 0; loop < length; ++loop) {
            sb.append(digits[rand.nextInt(digits.length)]);
        }
        return sb.toString();
    }

    public static String getEncryptionToken(String token) {
        for (int i = 0; i < 6; i++) {
            token = Base64Utils.encode(token.getBytes());
        }
        return token;
    }

    public static String generateRandomToken(){
        Random r;
        long rand1, rand2;

        r = new Random(System.currentTimeMillis());
        rand1 = Math.abs(r.nextLong());
        try { 
            Thread.sleep(rand1%100); 
        } catch(InterruptedException e){
        }
        
        rand2 = r.nextLong();
        return System.currentTimeMillis() + "-" +
            Math.abs(rand1) + "-" + Math.abs(rand2);
    }

}
