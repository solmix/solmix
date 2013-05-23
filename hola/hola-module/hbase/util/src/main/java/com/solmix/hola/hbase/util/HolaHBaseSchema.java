package com.solmix.hola.hbase.util;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.util.Bytes;


public class HolaHBaseSchema
{
    
    public static final byte[] TABLE_TYPE_PROPERTY = Bytes.toBytes("HolaTableType");
    public static final byte[] TABLE_TYPE_RECORD = Bytes.toBytes("record");

    public static enum Table {
        RECORD("record"),
        TYPE("type"),
        BLOBINCUBATOR("blobincubator");

        public final byte[] bytes;
        public final String name;

        Table(String name) {
            this.name = name;
            this.bytes = Bytes.toBytes(name);
        }
    }

    public static boolean isRecordTableDescriptor(HTableDescriptor descriptor) {
        byte[] value = descriptor.getValue(TABLE_TYPE_PROPERTY);
        return value != null && Bytes.equals(value, TABLE_TYPE_RECORD);
    }
}
