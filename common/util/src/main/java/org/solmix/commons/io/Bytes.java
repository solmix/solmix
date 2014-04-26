
package org.solmix.commons.io;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * copy from java.nio.Bits
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年2月10日
 */
public final class Bytes
{

    /**
     * Swap byte
     */
    public static short swap(short x) {
        return (short) ((x << 8) | ((x >> 8) & 0xff));
    }

    public static byte[] swap(byte[] x) {
        int i, count, len;
        byte tmp;
        len = x.length;
        count = len / 2;
        for (i = 0; i < count; i++) {
            tmp = x[i];
            x[i] = x[len - i - 1];
            x[len - i - 1] = tmp;
        }
        return x;
    }

    /**
     * Swap byte
     */
    public static char swap(char x) {
        return (char) ((x << 8) | ((x >> 8) & 0xff));
    }

    /**
     * Swap byte
     */
    public static int swap(int x) {
        return (swap((short) x) << 16) | (swap((short) (x >> 16)) & 0xffff);
    }

    /**
     * Swap byte
     */
    public static long swap(long x) {
        return ((long) swap((int) (x)) << 32) | (swap((int) (x >> 32)) & 0xffffffffL);
    }

    public static char makeChar(byte b1, byte b0) {
        return (char) ((b1 << 8) | (b0 & 0xff));
    }

    public static byte char1(char x) {
        return (byte) (x >> 8);
    }

    public static byte char0(char x) {
        return (byte) (x >> 0);
    }

    static public short makeShort(byte b1, byte b0) {
        return (short) ((b1 << 8) | (b0 & 0xff));
    }

    public static byte short1(short x) {
        return (byte) (x >> 8);
    }

    public static byte short0(short x) {
        return (byte) (x >> 0);
    }

    static public int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
    }

    static public long makeLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) {
        return ((((long) b7 & 0xff) << 56) | (((long) b6 & 0xff) << 48) | (((long) b5 & 0xff) << 40) | (((long) b4 & 0xff) << 32)
            | (((long) b3 & 0xff) << 24) | (((long) b2 & 0xff) << 16) | (((long) b1 & 0xff) << 8) | (((long) b0 & 0xff) << 0));
    }
    public static byte long7(long x) { return (byte)(x >> 56); }
    public static byte long6(long x) { return (byte)(x >> 48); }
    public static byte long5(long x) { return (byte)(x >> 40); }
    public static byte long4(long x) { return (byte)(x >> 32); }
    public static byte long3(long x) { return (byte)(x >> 24); }
    public static byte long2(long x) { return (byte)(x >> 16); }
    public static byte long1(long x) { return (byte)(x >>  8); }
    public static byte long0(long x) { return (byte)(x >>  0); }

    public static byte int3(int x) {
        return (byte) (x >> 24);
    }

    public static byte int2(int x) {
        return (byte) (x >> 16);
    }

    public static byte int1(int x) {
        return (byte) (x >> 8);
    }

    public static byte int0(int x) {
        return (byte) (x >> 0);
    }
    /**
     * Formate space split Hex string to byte.
     * @param str
     * @return
     */
    public static byte[] formatStringToByte(String str){
        if(str==null)return null;
        str=str.trim();
       
        while(str.indexOf("  ")!=-1){
            str.replace("  ", " ");
        }
        String[] bs=str.split(" ");
        byte[] res = new byte[bs.length];
        for(int i=0;i<bs.length;i++){
           res[i]= Short.valueOf(bs[i],16).byteValue();
        }
        return res;
    }

    public static String byteToHexString(byte... src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append(" ");
        }
        return stringBuilder.toString();
    }

    public static int add(int a, int b) {
        int carry, add;
        do {
            add = a ^ b;
            carry = (a & b) << 1;
            a = add;
            b = carry;
        } while (carry != 0);
        return add;
    }

    public static int subtract(int a, int b) {
        return add(a, add(~b, 1));
    }

    public static int divide(int a, int b) {
        boolean neg = (a > 0) ^ (b > 0);
        if (a < 0)
            a = -a;
        if (b < 0)
            b = -b;
        if (a < b)
            return 0;
        int msb = 0;
        for (msb = 0; msb < 32; msb++) {
            if ((b << msb) >= a)
                break;
        }
        int q = 0;
        for (int i = msb; i >= 0; i--) {
            if ((b << i) > a)
                continue;
            q |= (1 << i);
            a -= (b << i);
        }
        if (neg)
            return -q;
        return q;
    }

    /**
     * @param buffers
     */
    public static String byteToHexString(List<ByteBuffer> buffers) {
        StringBuffer sb= new StringBuffer();
        for(ByteBuffer bf:buffers){
            bf.rewind();
            byte[] target= new byte[bf.limit()];
            bf.get(target);
            sb.append(byteToHexString(target));
        }
        return sb.toString();
    }
    public static int  BCDToInt(byte bcd){
        return (0xff & (bcd>>4))*10 +(0xf & bcd);
    }
    public static byte  IntToBCD2(int i){
        return (byte) ((i/10)<<4|(i%10&0x0f));
    }
    public static byte[]  IntToBCD4(int i){
        byte[] bytes= new byte[2];
        bytes[0]= (byte) ((i/10)<<4|(i%10&0x0f));
        bytes[1]= (byte) ((i/1000)<<4|(i/100));
        return bytes;
    }
}
