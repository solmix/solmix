
package org.solmix.commons.util;

/**
 * copy from java.nio.Bits
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年2月10日
 */
public class Bytes
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

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
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
}
