package org.solmix.commons.util;

import java.lang.reflect.Array;


public final class HashUtils
{
    /**
     * An initial value for a <code>hashCode</code>, to which is added
     * contributions from fields. Using a non-zero value decreases collisons of
     * <code>hashCode</code> values.
     */
    public static final int SEED = 23;
    private static final int ODD_PRIME_NUMBER = 37;

    public static int hash(int seed, boolean b) {
        return firstTerm(seed) + (b ? 1 : 0);
    }

    public static int hash(int seed, char c) {
        return firstTerm(seed) + c;
    }

    public static int hash(int seed, int i) {
        /*
         * Implementation Note Note that byte and short are handled by this
         * method, through implicit conversion.
         */
        return firstTerm(seed) + i;
    }

    public static int hash(int seed, long l) {
        return firstTerm(seed) + (int) (l ^ (l >>> 32));
    }

    public static int hash(int seed, float f) {
        return hash(seed, Float.floatToIntBits(f));
    }

    public static int hash(int seed, double d) {
        return hash(seed, Double.doubleToLongBits(d));
    }

    /**
     * <code>aObject</code> is a possibly-null object field, and possibly an array.
     * 
     * <p>If <code>aObject</code> is an array, then each element may be a primitive or a possibly-null object.
     *
     * @param seed
     *            the seed
     * @param o
     *            the object
     * @return the hash code for an object
     */
    public static int hash(int seed, Object o) {
        int result = seed;
        if (o == null) {
            result = hash(result, 0);
        } else if (!isArray(o)) {
            result = hash(result, o.hashCode());
        } else {
            int length = Array.getLength(o);
            for (int idx = 0; idx < length; ++idx) {
                Object item = Array.get(o, idx);
                // recursive call!
                result = hash(result, item);
            }
        }
        return result;
    }

    private static int firstTerm(int seed) {
        return ODD_PRIME_NUMBER * seed;
    }

    private static boolean isArray(Object anObject) {
        return anObject.getClass().isArray();
    }
}
