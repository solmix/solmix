/*
 * Copyright 2013 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.ipc.avro;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.AvroRuntimeException;

/**
 * Data structure, encoder and decoder classes for the Netty transport. adapte netty 5.0
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年2月16日
 */

public class MyNettyCodec
{

    /**
     * Transport protocol data structure when using Netty.
     */
    public static class NettyDataPack
    {

        private int serial; // to track each call in client side

        private List<ByteBuffer> datas;

        public NettyDataPack()
        {
        }

        public NettyDataPack(int serial, List<ByteBuffer> datas)
        {
            this.serial = serial;
            this.datas = datas;
        }

        public void setSerial(int serial) {
            this.serial = serial;
        }

        public int getSerial() {
            return serial;
        }

        public void setDatas(List<ByteBuffer> datas) {
            this.datas = datas;
        }

        public List<ByteBuffer> getDatas() {
            return datas;
        }
    }

    /**
     * Protocol encoder which converts NettyDataPack which contains the Responder's output List&lt;ByteBuffer&gt; to
     * ByteBuf needed by Netty.
     */
    public static class NettyFrameEncoder extends MessageToByteEncoder<NettyDataPack>
    {

        /**
         * {@inheritDoc}
         * 
         * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext,
         *      java.lang.Object, io.netty.buffer.ByteBuf)
         */
        @Override
        protected void encode(ChannelHandlerContext ctx, NettyDataPack dataPack, ByteBuf out) throws Exception {
            List<ByteBuffer> origs = dataPack.getDatas();
            out.writeBytes(getPackHeader(dataPack)); // prepend a pack header including serial number and list size
            for (ByteBuffer b : origs) {
                out.writeBytes(getLengthHeader(b));// for each buffer prepend length field
                out.writeBytes(b);
            }
        }
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            super.write(ctx, msg, promise);
            ctx.flush();
        }

        private ByteBuffer getLengthHeader(ByteBuffer buf) {
            ByteBuffer header = ByteBuffer.allocate(4);
            header.putInt(buf.limit());
            header.flip();
            return header;
        }

        private ByteBuffer getPackHeader(NettyDataPack dataPack) {
            ByteBuffer header = ByteBuffer.allocate(8);
            header.putInt(dataPack.getSerial());
            header.putInt(dataPack.getDatas().size());
            header.flip();
            return header;
        }

    }

    /**
     * Protocol decoder which converts Netty's ByteBuf to NettyDataPack which contains a List&lt;ByteBuffer&gt; needed
     * by Avro Responder.
     */
    public static class NettyFrameDecoder extends ByteToMessageDecoder
    {

        private boolean packHeaderRead = false;

        private int listSize;

        private NettyDataPack dataPack;

        private final long maxMem;

        private static final long SIZEOF_REF = 8L; // mem usage of 64-bit pointer

        public NettyFrameDecoder()
        {
            maxMem = Runtime.getRuntime().maxMemory();
        }

        /**
         * {@inheritDoc}
         * 
         * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext,
         *      io.netty.buffer.ByteBuf, java.util.List)
         */
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (!packHeaderRead) {
                if (decodePackHeader(ctx, in)) {
                    packHeaderRead = true;
                }
                return;
            } else {
                if (decodePackBody(ctx, in)) {
                    packHeaderRead = false; // reset state
                    out.add(dataPack);
                } else {
                    return;
                }
            }
        }

        private boolean decodePackBody(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
            if (in.readableBytes() < 4) {
                return false;
              }
            in.markReaderIndex();
            
            int length = in.readInt();

            if (in.readableBytes() < length) {
                in.resetReaderIndex();
              return false;
            }

            ByteBuffer bb = ByteBuffer.allocate(length);
            in.readBytes(bb);
            bb.flip();
            dataPack.getDatas().add(bb);
            
            return dataPack.getDatas().size()==listSize;
        }

        private boolean decodePackHeader(ChannelHandlerContext ctx, ByteBuf in)throws Exception {
            if (in.readableBytes()<8) {
                return false;
              }

              int serial = in.readInt();
              int listSize = in.readInt();

              // Sanity check to reduce likelihood of invalid requests being honored.
              // Only allow 10% of available memory to go towards this list (too much!)
              if (listSize * SIZEOF_REF > 0.1 * maxMem) {
                ctx.channel().close().await();
                throw new AvroRuntimeException("Excessively large list allocation " +
                    "request detected: " + listSize + " items! Connection closed.");
              }

              this.listSize = listSize;
              dataPack = new NettyDataPack(serial, new ArrayList<ByteBuffer>(listSize));

              return true;
        }

    }
}
