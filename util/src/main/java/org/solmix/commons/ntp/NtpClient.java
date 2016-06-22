package org.solmix.commons.ntp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NtpClient {
	private static final int DEFAULT_TIMEOUT = 10000;
	private static final int DEFAULT_PORT = 123;

	private static Logger log = LoggerFactory.getLogger(NtpClient.class);

	private String hostname;
	private int port;
	private int timeout;

	public NtpClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.timeout = DEFAULT_TIMEOUT;
	}

	public NtpClient(String hostname) {
		this(hostname, DEFAULT_PORT);
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Return the number of seconds since 1900
	 */
	protected static double now() {
		return (double) (System.currentTimeMillis() / 1000.0) + 2208988800.0;
	}

	public NtpResponse getResponse() throws SocketException,
			UnknownHostException, IOException {
		DatagramSocket socket = new DatagramSocket();
		try{
		socket.setSoTimeout(this.timeout);

		InetAddress address = InetAddress.getByName(this.hostname);
		byte[] data = NtpResponse.getRequestBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, address,
				this.port);
		socket.send(packet);

		packet = new DatagramPacket(data, data.length);
		socket.receive(packet);

		NtpResponse response = NtpResponse.decodeResponse(now(),
				packet.getData());
		return response;
		}finally{
			if(socket!=null){
				socket.close();
			}
			
		}
		
	}
}
