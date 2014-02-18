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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.ipc.avro.MyNettyCodec.NettyDataPack;
import org.solmix.ipc.avro.MyNettyCodec.NettyFrameDecoder;
import org.solmix.ipc.avro.MyNettyCodec.NettyFrameEncoder;

/**
 * A Netty-based RPC {@link Server} implementation,adapte netty 5.0
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年2月16日
 */

public class MyNettyServer implements Server
{

    private static final Logger LOG = LoggerFactory.getLogger(MyNettyServer.class.getName());

    private static final int DEFAULT_BOSSES = 2;

    private static final int DEFAULT_WORKERS = 3;

    private final Responder responder;

    private final Channel serverChannel;

    private final ChannelGroup allChannels = new DefaultChannelGroup(
        "avro-my-server", GlobalEventExecutor.INSTANCE);

    private final EventLoopGroup accepter;

    private final EventLoopGroup connector;

    private final CountDownLatch closed = new CountDownLatch(1);

    /**
     * 
     * @param executionHandler if not null, will be inserted into the Netty
     *        pipeline. Use this when your responder does long, non-cpu bound
     *        processing (see Netty's ExecutionHandler javadoc).
     * @param pipelineFactory Avro-related handlers will be added on top of what
     *        this factory creates
     */
    public MyNettyServer(Responder responder, InetSocketAddress addr,
        Class<? extends ServerChannel> channelClass,
        final EventLoopGroup accepter, final EventLoopGroup connector,
        ChannelHandler... handlers)
    {
        this.responder = responder;
        this.connector = connector;
        this.accepter = accepter;
        ServerBootstrap b = new ServerBootstrap();
        b.channel(channelClass);
        b.group(accepter, accepter);
        b.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new NettyFrameDecoder());
                pipeline.addLast("frameEncoder", new NettyFrameEncoder());
                pipeline.addLast("handler", new NettyServerAvroHandler());

            }

        });
        serverChannel = b.bind(addr).channel();
        allChannels.add(serverChannel);

    }

    public MyNettyServer(Responder responder, InetSocketAddress addr)
    {
        this(responder, addr, new ChannelHandler[] {});
    }

    public MyNettyServer(Responder responder, InetSocketAddress addr,
        ChannelHandler... handlers)
    {
        this(responder, addr,
            io.netty.channel.socket.nio.NioServerSocketChannel.class,
            new NioEventLoopGroup(DEFAULT_BOSSES, new DefaultThreadFactory(
                "Avro-nio-boss", true)), new NioEventLoopGroup(DEFAULT_WORKERS,
                new DefaultThreadFactory("Avro-nio-worker", true)), handlers);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.avro.ipc.Server#close()
     */
    @Override
    public void close() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();
        if (this.accepter != null)
            accepter.shutdownGracefully();
        if (this.connector != null)
            connector.shutdownGracefully();
        closed.countDown();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.avro.ipc.Server#getPort()
     */
    @Override
    public int getPort() {
        return ((InetSocketAddress) serverChannel.localAddress()).getPort();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.avro.ipc.Server#join()
     */
    @Override
    public void join() throws InterruptedException {
        closed.await();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.avro.ipc.Server#start()
     */
    @Override
    public void start() {
        // No-op.
    }

    /**
     * 
     * @return The number of clients currently connected to this server.
     */
    public int getNumActiveConnections() {
        // allChannels also contains the server channel, so exclude that from
        // the count.
        return allChannels.size() - 1;
    }

    public class NettyServerAvroHandler extends
        SimpleChannelInboundHandler<NettyDataPack>
    {

        private final MyNettyTransceiver connectionMetadata = new MyNettyTransceiver();

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            allChannels.add(ctx.channel());
            super.channelActive(ctx);
        }

        /**
         * {@inheritDoc}
         * 
         * @see io.netty.channel.SimpleChannelInboundHandler#messageReceived
         *      (io.netty.channel.ChannelHandlerContext, java.lang.Object)
         */
        @Override
        protected void messageReceived(ChannelHandlerContext ctx,
            NettyDataPack dataPack) throws Exception {
            try {
                List<ByteBuffer> req = dataPack.getDatas();
                List<ByteBuffer> res = responder.respond(req,
                    connectionMetadata);
                // response will be null for oneway messages.
                if (res != null) {
                    dataPack.setDatas(res);
                    ctx.write(dataPack);
                }
            } catch (IOException ex) {
                LOG.warn("unexpect error");
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
            LOG.warn("Unexpected exception from downstream.", cause);
            ctx.close();
            allChannels.remove(ctx.channel());
            // ctx.fireExceptionCaught(cause);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOG.info("Connection to {} disconnected.",
                ctx.channel().remoteAddress());
            super.channelInactive(ctx);
            ctx.channel().close();
            allChannels.remove(ctx.channel());
        }
    }
}
