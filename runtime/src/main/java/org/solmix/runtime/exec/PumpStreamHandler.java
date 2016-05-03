
package org.solmix.runtime.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copies standard output and error of subprocesses to standard output and
 * error of the parent process.
 *
 *
 * @author thomas.haas@softwired-inc.com
 */
public class PumpStreamHandler implements ExecuteStreamHandler {

    private Thread inputThread;
    private Thread errorThread;

    private OutputStream out, err;
    boolean running=false;

    public PumpStreamHandler(OutputStream out, OutputStream err) {
        this.out = out;
        this.err = err;
    }

    public PumpStreamHandler(OutputStream outAndErr) {
        this(outAndErr, outAndErr);
    }

    public PumpStreamHandler() {
        this(System.out, System.err);
    }

    @Override
    public void setProcessOutputStream(InputStream is) {
        createProcessOutputPump(is, out);
    }


    @Override
    public void setProcessErrorStream(InputStream is) {
        createProcessErrorPump(is, err);
    }


    /**
     * Standard input is not processed. So we are closing the stream as required for the Windows powershell scenario.
     */
    @Override
    public void setProcessInputStream(OutputStream os) {
        try {
            os.close();
        } catch (IOException e) {
            // not a problem in this case.
        }
    }


    @Override
    public void start() {
        inputThread.start();
        errorThread.start();
        running=true;
    }


    @Override
    public void stop() {
        if( ! running ) return;
        try {
            //if( log.isDebugEnabled() ) log.debug("Joining it");
            //            inputThread.interrupt();
            inputThread.join(1000);
            //if( log.isDebugEnabled() ) log.debug("Joined" );
        } catch(InterruptedException e) {}
        try {
            //if( log.isDebugEnabled() ) log.debug("Joining it");
            //            errorThread.interrupt();
            errorThread.join(1000);
            //if( log.isDebugEnabled() ) log.debug("Joined" );
        } catch(InterruptedException e) {}
        try {
            err.flush();
        } catch (IOException e) {}
        try {
            out.flush();
        } catch (IOException e) {}
        running=false;
    }

    protected OutputStream getErr() {
        return err;
    }

    protected OutputStream getOut() {
        return out;
    }

    protected void createProcessOutputPump(InputStream is, OutputStream os) {
        inputThread = createPump(is, os);
    }

    protected void createProcessErrorPump(InputStream is, OutputStream os) {
        errorThread = createPump(is, os);
    }


    /**
     * Creates a stream pumper to copy the given input stream to the given output stream.
     */
    protected Thread createPump(InputStream is, OutputStream os) {
        final Thread result = new Thread(new StreamPumper(is, os));
        result.setDaemon(true);
        return result;
    }

    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( PumpStreamHandler.class );

}
