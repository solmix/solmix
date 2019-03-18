
package org.solmix.commons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

import org.solmix.commons.util.DataUtils;

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
    public static short swap(final short wrap) {
        return (short) ((wrap << 8) | ((wrap >> 8) & 0xff));
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
        	str=str.replace("  ", " ");
        }
        String[] bs=str.split(" ");
        byte[] res = new byte[bs.length];
        for(int i=0;i<bs.length;i++){
           res[i]= Short.valueOf(bs[i],16).byteValue();
        }
        return res;
    }

    public static String bytesToHexString(byte... src) {
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
        int msb ;
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
            sb.append(bytesToHexString(target));
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

    public static int bytes2int(byte[] b, int off)
    {
          return ((b[off + 3] & 0xFF) << 0) +
           ((b[off + 2] & 0xFF) << 8) +
           ((b[off + 1] & 0xFF) << 16) +
           ((b[off + 0]) << 24);
    }
    public static int bytes2int(byte[] b)
    {
          return ((b[3] & 0xFF) << 0) +
           ((b[2] & 0xFF) << 8) +
           ((b[ 1] & 0xFF) << 16) +
           ((b[0]) << 24);
    }
    /**
     * @param header
     * @param i
     * @return
     */
    public static long bytes2long(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL) << 0) +
            ((b[off + 6] & 0xFFL) << 8) +
            ((b[off + 5] & 0xFFL) << 16) +
            ((b[off + 4] & 0xFFL) << 24) +
            ((b[off + 3] & 0xFFL) << 32) +
            ((b[off + 2] & 0xFFL) << 40) +
            ((b[off + 1] & 0xFFL) << 48) +
            (((long) b[off + 0]) << 56);
    }

  
    public static void short2bytes(short v, byte[] b) {
        short2bytes(v, b, 0);
        
    }
    public static void short2bytes(short v, byte[] b, int off)
    {
          b[off + 1] = (byte) v;
          b[off + 0] = (byte) (v >>> 8);
    }
    public static void long2bytes(long v, byte[] b)
    {
          long2bytes(v, b, 0);
    }

 
    public static void long2bytes(long v, byte[] b, int off)
    {
          b[off + 7] = (byte) v;
          b[off + 6] = (byte) (v >>> 8);
          b[off + 5] = (byte) (v >>> 16);
          b[off + 4] = (byte) (v >>> 24);
          b[off + 3] = (byte) (v >>> 32);
          b[off + 2] = (byte) (v >>> 40);
          b[off + 1] = (byte) (v >>> 48);
          b[off + 0] = (byte) (v >>> 56);
    }

    public static void int2bytes(int v, byte[] b, int off)
    {
          b[off + 3] = (byte) v;
          b[off + 2] = (byte) (v >>> 8);
          b[off + 1] = (byte) (v >>> 16);
          b[off + 0] = (byte) (v >>> 24);
    }
    public static byte[] toBcdBits(String v) {
        return DataUtils.reversal(str2Bcd(v));
    }
    /**
     * BCD to Integer String
     * 
     * @param bytes
     * @return
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
    }

    public static byte[] hexStringToByte(String hex) {
        // return hex.getBytes();
        hex=hex.toUpperCase().replace(" ","");
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static final Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(in);
        Object o = oi.readObject();
        oi.close();
        return o;
    }

    public static final byte[] objectToBytes(Serializable s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream ot = new ObjectOutputStream(out);
        ot.writeObject(s);
        ot.flush();
        ot.close();
        return out.toByteArray();
    }

    public static final String objectToHexString(Serializable s) throws IOException {
        return bytesToHexString(objectToBytes(s));
    }

    public static final Object hexStringToObject(String hex) throws IOException, ClassNotFoundException {
        return bytesToObject(hexStringToByte(hex));
    }


    public static byte[] toBcdBitsCopy(byte[] target, String v) {
        return DataUtils.reversalCopy(target, str2Bcd(v));
    }
    /**
     * Integer String to BCD
     * 
     * @param asc
     * @return
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static byte[] copyOf(byte[] src, int length)
    {
        byte[] dest = new byte[length];
      System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
      return dest;
    }
    
  

}
