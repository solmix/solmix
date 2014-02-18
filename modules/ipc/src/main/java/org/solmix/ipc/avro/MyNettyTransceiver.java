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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.avro.Protocol;
import org.apache.avro.ipc.CallFuture;
import org.apache.avro.ipc.Callback;
import org.apache.avro.ipc.Transceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.ipc.avro.MyNettyCodec.NettyDataPack;
import org.solmix.ipc.avro.MyNettyCodec.NettyFrameDecoder;
import org.solmix.ipc.avro.MyNettyCodec.NettyFrameEncoder;

/**
 * A Netty-based {@link Transceiver} implementation.adapte netty 5.0+. Copy and
 * modification from apache avro NettyTransceiver.java.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年2月16日
 */

public class MyNettyTransceiver extends Transceiver
{

    /** If not specified, the default connection timeout will be used (60 sec). */
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 60 * 1000;

    public static final String NETTY_CONNECT_TIMEOUT_OPTION = "connectTimeoutMillis";

    public static final String NETTY_TCP_NODELAY_OPTION = "tcpNoDelay";

    public static final String NETTY_KEEPALIVE_OPTION = "keepAlive";

    public static final Boolean DEFAULT_TCP_NODELAY_VALUE = true;

    private static final Logger LOG = LoggerFactory.getLogger(MyNettyTransceiver.class.getName());

    private final Map<Integer, Callback<List<ByteBuffer>>> requests = new ConcurrentHashMap<Integer, Callback<List<ByteBuffer>>>();

    private final AtomicInteger serialGenerator = new AtomicInteger(0);

    private final EventLoopGroup eventLoop;

    private final int connectTimeoutMillis;

    private final Bootstrap bootstrap;

    private final InetSocketAddress remoteAddr;

    volatile ChannelFuture channelFuture;

    volatile boolean stopping;

    private final Object channelFutureLock = new Object();

    /**
     * Read lock must be acquired whenever using non-final state. Write lock
     * must be acquired whenever modifying state.
     */
    private final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();

    private Channel channel; // Synchronized on stateLock

    private Protocol remote; // Synchronized on stateLock

    MyNettyTransceiver()
    {
        eventLoop = null;
        connectTimeoutMillis = 0;
        bootstrap = null;
        remoteAddr = null;
        channelFuture = null;
    }

    /**
     * Creates a NettyTransceiver, and attempts to connect to the given address.
     * {@link #DEFAULT_CONNECTION_TIMEOUT_MILLIS} is used for the connection
     * timeout.
     * 
     * @param addr the address to connect to.
     * @throws IOException if an error occurs connecting to the given address.
     */
    public MyNettyTransceiver(InetSocketAddress addr) throws IOException
    {
        this(addr, DEFAULT_CONNECTION_TIMEOUT_MILLIS);
    }

    /**
     * Creates a NettyTransceiver, and attempts to connect to the given address.
     * 
     * @param addr the address to connect to.
     * @param connectTimeoutMillis maximum amount of time to wait for connection
     *        establishment in milliseconds, or null to use
     *        {@link #DEFAULT_CONNECTION_TIMEOUT_MILLIS}.
     * @throws IOException if an error occurs connecting to the given address.
     */
    public MyNettyTransceiver(InetSocketAddress addr,
        Integer connectTimeoutMillis) throws IOException
    {
        this(addr, connectTimeoutMillis,new ChannelHandler[]{});
    }

    public MyNettyTransceiver(InetSocketAddress addr,
        Integer connectTimeoutMillis,ChannelHandler... handlers) throws IOException
    {
        this(addr, io.netty.channel.socket.nio.NioSocketChannel.class,
             new NioEventLoopGroup(), connectTimeoutMillis,handlers);
    }

    /**
     * Creates a NettyTransceiver, and attempts to connect to the given address.
     * It is strongly recommended that the {@link #NETTY_CONNECT_TIMEOUT_OPTION}
     * option be set to a reasonable timeout value (a Long value in
     * milliseconds) to prevent connect/disconnect attempts from hanging
     * indefinitely. It is also recommended that the
     * {@link #NETTY_TCP_NODELAY_OPTION} option be set to true to minimize RPC
     * latency.
     * 
     * @param addr the address to connect to.
     * @param channelFactory the factory to use to create a new Netty Channel.
     * @param nettyClientBootstrapOptions map of Netty ClientBootstrap options
     *        to use.
     * @throws IOException if an error occurs connecting to the given address.
     */
    @SuppressWarnings("unchecked")
    public <T> MyNettyTransceiver(InetSocketAddress addr,
        Class<? extends Channel> channelClass,
        final EventLoopGroup eventLoop, Map<ChannelOption<?>, ?> configs,
        final ChannelHandler... handlers) throws IOException
    {
        this.eventLoop = eventLoop;
        this.connectTimeoutMillis = (Integer) configs.get(ChannelOption.CONNECT_TIMEOUT_MILLIS);
        bootstrap = new Bootstrap();
        remoteAddr = addr;
        bootstrap.group(eventLoop);
        bootstrap.channel(channelClass);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    if (handlers != null) {
                        for (ChannelHandler handler : handlers) {
                            p.addLast(handler.getClass().getSimpleName(),
                                handler);
                        }
                    }
                    p.addLast("frameDecoder", new NettyFrameDecoder());
                    p.addLast("frameEncoder", new NettyFrameEncoder());
                    p.addLast("handler", createNettyClientAvroHandler());
                }

            });
        if (configs != null) {
            for (Entry<ChannelOption<?>, ?> e : configs.entrySet()) {
                bootstrap.option((ChannelOption<Object>) e.getKey(),
                    e.getValue());
            }
        }
        // bootstrap.
        // Make a new connection.
        stateLock.readLock().lock();
        try {
            getChannel();
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public MyNettyTransceiver(InetSocketAddress addr,
        Class<? extends Channel> clz,
        NioEventLoopGroup eventLoop, Integer connectTimeoutMillis,ChannelHandler... handlers)
        throws IOException
    {
        this(addr, clz, eventLoop,
            buildDefaultBootstrapOptions(connectTimeoutMillis),new ChannelHandler[]{});
    }

    /**
     * Creates a Netty ChannelUpstreamHandler for handling events on the Netty
     * client channel.
     * 
     * @return the ChannelUpstreamHandler to use.
     */
    protected NettyClientAvroHandler createNettyClientAvroHandler() {
        return new NettyClientAvroHandler();
    }

    /**
     * Creates the default options map for the Netty ClientBootstrap.
     * 
     * @param connectTimeoutMillis connection timeout in milliseconds, or null
     *        if no timeout is desired.
     * @return the map of Netty bootstrap options.
     */
    protected static Map<ChannelOption<?>, ?> buildDefaultBootstrapOptions(
        Integer connectTimeoutMillis) {
        Map<ChannelOption<?>, Object> config = new IdentityHashMap<ChannelOption<?>, Object>(
            3);
        config.put(ChannelOption.TCP_NODELAY, DEFAULT_TCP_NODELAY_VALUE);
        config.put(ChannelOption.SO_KEEPALIVE, true);
        config.put(ChannelOption.CONNECT_TIMEOUT_MILLIS,
            connectTimeoutMillis == null ? DEFAULT_CONNECTION_TIMEOUT_MILLIS
                : connectTimeoutMillis);
        return config;
    }

    /**
     * Gets the Netty channel. If the channel is not connected, first attempts
     * to connect. NOTE: The stateLock read lock *must* be acquired before
     * calling this method.
     * 
     * @return the Netty channel
     * @throws IOException if an error occurs connecting the channel.
     */
    private Channel getChannel() throws IOException {
        if (!isChannelReady(channel)) {
            // Need to reconnect
            // Upgrade to write lock
            stateLock.readLock().unlock();
            stateLock.writeLock().lock();
            try {
                if (!isChannelReady(channel)) {

                    synchronized (channelFutureLock) {
                        if (!stopping) {
                            LOG.debug("Connecting to " + remoteAddr);
                            channelFuture = bootstrap.connect(remoteAddr);
                        }
                    }
                    if (channelFuture != null) {
                        try {
                            channelFuture.await(connectTimeoutMillis);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Reset
                                                                // interrupt
                                                                // flag
                            throw new IOException(
                                "Interrupted while connecting to " + remoteAddr);
                        }

                        synchronized (channelFutureLock) {
                            if (!channelFuture.isSuccess()) {
                                throw new IOException("Error connecting to "
                                    + remoteAddr, channelFuture.cause());
                            }
                            channel = channelFuture.channel();
                            channelFuture = null;
                        }
                    }
                }
            } finally {
                // Downgrade to read lock:
                stateLock.readLock().lock();
                stateLock.writeLock().unlock();
            }
        }
        return channel;
    }

    /**
     * Tests whether the given channel is ready for writing.
     * 
     * @return true if the channel is open and ready; false otherwise.
     */
    private static boolean isChannelReady(Channel channel) {
        return (channel != null) && channel.isOpen() && channel.isActive();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.avro.ipc.Transceiver#getRemoteName()
     */
    @Override
    public String getRemoteName() throws IOException {
        stateLock.readLock().lock();
        try {
            return getChannel().remoteAddress().toString();
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @Override
    public Protocol getRemote() {
        stateLock.readLock().lock();
        try {
            return remote;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @Override
    public boolean isConnected() {
        stateLock.readLock().lock();
        try {
            return remote != null;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @Override
    public void setRemote(Protocol protocol) {
        stateLock.writeLock().lock();
        try {
            this.remote = protocol;
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    @Override
    public List<ByteBuffer> readBuffers() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Netty channels are thread-safe, so there is no need to acquire locks.
     * This method is a no-op.
     */
    @Override
    public void lockChannel() {

    }

    /**
     * Netty channels are thread-safe, so there is no need to acquire locks.
     * This method is a no-op.
     */
    @Override
    public void unlockChannel() {

    }

    /**
     * Closes this transceiver and disconnects from the remote peer. Cancels all
     * pending RPCs, sends an IOException to all pending callbacks, and blocks
     * until the close has completed.
     */
    @Override
    public void close() {
        close(true);
    }

    /**
     * Closes this transceiver and disconnects from the remote peer. Cancels all
     * pending RPCs and sends an IOException to all pending callbacks.
     * 
     * @param awaitCompletion if true, will block until the close has completed.
     */
    public void close(boolean awaitCompletion) {
        try {
            // Close the connection:
            stopping = true;
            disconnect1(awaitCompletion, true, null);
        } finally {
            // Shut down all thread pools to exit.
            this.eventLoop.shutdownGracefully();
        }
    }

    /**
     * Closes the connection to the remote peer if connected.
     * 
     * @param awaitCompletion if true, will block until the close has completed.
     * @param cancelPendingRequests if true, will drain the requests map and
     *        send an IOException to all Callbacks.
     * @param cause if non-null and cancelPendingRequests is true, this
     *        Throwable will be passed to all Callbacks.
     */
    private void disconnect1(boolean awaitCompletion,
        boolean cancelPendingRequests, Throwable cause) {
        Channel channelToClose = null;
        Map<Integer, Callback<List<ByteBuffer>>> requestsToCancel = null;
        boolean stateReadLockHeld = stateLock.getReadHoldCount() != 0;

        ChannelFuture channelFutureToCancel = null;
        synchronized (channelFutureLock) {
            if (stopping && channelFuture != null) {
                channelFutureToCancel = channelFuture;
                channelFuture = null;
            }
        }
        if (channelFutureToCancel != null) {
            channelFutureToCancel.cancel(true);
        }

        if (stateReadLockHeld) {
            stateLock.readLock().unlock();
        }
        stateLock.writeLock().lock();
        try {
            if (channel != null) {
                if (cause != null) {
                    LOG.debug("Disconnecting from " + remoteAddr, cause);
                } else {
                    LOG.debug("Disconnecting from " + remoteAddr);
                }
                channelToClose = channel;
                channel = null;
                remote = null;
                if (cancelPendingRequests) {
                    // Remove all pending requests (will be canceled after
                    // relinquishing
                    // write lock).
                    requestsToCancel = new ConcurrentHashMap<Integer, Callback<List<ByteBuffer>>>(
                        requests);
                    requests.clear();
                }
            }
        } finally {
            if (stateReadLockHeld) {
                stateLock.readLock().lock();
            }
            stateLock.writeLock().unlock();
        }

        // Cancel any pending requests by sending errors to the callbacks:
        if ((requestsToCancel != null) && !requestsToCancel.isEmpty()) {
            LOG.debug("Removing " + requestsToCancel.size()
                + " pending request(s).");
            for (Callback<List<ByteBuffer>> request : requestsToCancel.values()) {
                request.handleError(cause != null ? cause : new IOException(
                    getClass().getSimpleName() + " closed"));
            }
        }

        // Close the channel:
        if (channelToClose != null) {
            ChannelFuture closeFuture = channelToClose.close();
            if (awaitCompletion && (closeFuture != null)) {
                try {
                    closeFuture.await(connectTimeoutMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Reset interrupt flag
                    LOG.warn("Interrupted while disconnecting", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.avro.ipc.Transceiver#writeBuffers(java.util.List)
     */
    @Override
    public void writeBuffers(List<ByteBuffer> buffers) throws IOException {
        ChannelFuture writeFuture;
        stateLock.readLock().lock();
        try {
            writeFuture = writeDataPack(new org.solmix.ipc.avro.MyNettyCodec.NettyDataPack(
                serialGenerator.incrementAndGet(), buffers));
        } finally {
            stateLock.readLock().unlock();
        }

        if (!writeFuture.isDone()) {
            try {
                writeFuture.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Reset interrupt flag
                throw new IOException(
                    "Interrupted while writing Netty data pack", e);
            }
        }
        if (!writeFuture.isSuccess()) {
            throw new IOException("Error writing buffers", writeFuture.cause());
        }

    }

    /**
     * Writes a NettyDataPack, reconnecting to the remote peer if necessary.
     * NOTE: The stateLock read lock *must* be acquired before calling this
     * method.
     * 
     * @param dataPack the data pack to write.
     * @return the Netty ChannelFuture for the write operation.
     * @throws IOException if an error occurs connecting to the remote peer.
     */
    private ChannelFuture writeDataPack(NettyDataPack dataPack)
        throws IOException {
        return getChannel().write(dataPack);
    }

    /**
     * Override as non-synchronized method because the method is thread safe.
     */
    @Override
    public List<ByteBuffer> transceive(List<ByteBuffer> request)
        throws IOException {
        try {
            CallFuture<List<ByteBuffer>> transceiverFuture = new CallFuture<List<ByteBuffer>>();
            transceive(request, transceiverFuture);
            return transceiverFuture.get();
        } catch (InterruptedException e) {
            LOG.debug("failed to get the response", e);
            return null;
        } catch (ExecutionException e) {
            LOG.debug("failed to get the response", e);
            return null;
        }
    }

    @Override
    public void transceive(List<ByteBuffer> request,
        Callback<List<ByteBuffer>> callback) throws IOException {
        stateLock.readLock().lock();
        try {
            int serial = serialGenerator.incrementAndGet();
            NettyDataPack dataPack = new NettyDataPack(serial, request);
            requests.put(serial, callback);
            writeDataPack(dataPack);
        } finally {
            stateLock.readLock().unlock();
        }
    }

    /**
     * Avro client handler for the Netty transport
     */
    protected class NettyClientAvroHandler extends
        SimpleChannelInboundHandler<NettyDataPack>
    {

        /**
         * {@inheritDoc}
         * 
         * @see io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty.channel.ChannelHandlerContext,
         *      java.lang.Object)
         */
        @Override
        protected void messageReceived(ChannelHandlerContext ctx,
            NettyDataPack dataPack) throws Exception {
            Callback<List<ByteBuffer>> callback = requests.get(dataPack.getSerial());
            if (callback == null) {
                throw new RuntimeException("Missing previous call info");
            }
            try {
                callback.handleResult(dataPack.getDatas());
            } finally {
                requests.remove(dataPack.getSerial());
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
            disconnect1(false, true, cause);
        }
    }
}
