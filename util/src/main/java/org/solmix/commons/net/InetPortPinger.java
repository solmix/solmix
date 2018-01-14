
package org.solmix.commons.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InetPortPinger
{

    private String host;

    private int port;

    private int timeout;

    /**
     * Create a new InetPortPinger object.
     * 
     * @param host Hostname to ping
     * @param port Port # to ping
     * @param timeout Timeout (in ms) to wait for data, before aborting
     */
    public InetPortPinger(String host, int port, int timeout)
    {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Pings the remote host. A connection is made to the TCP port as specified in the constructor.
     *
     * @return true if connection was successful, false otherwise.
     */
    public boolean check() {
        try {
            Socket s = new Socket();
            s.bind(new InetSocketAddress(0));
            s.connect(new InetSocketAddress(host, port), timeout);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        InetPortPinger p = new InetPortPinger(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));

        System.out.println(p.check());
    }
}
