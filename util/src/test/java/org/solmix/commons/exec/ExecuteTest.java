package org.solmix.commons.exec;

import static org.junit.Assert.*;

import org.junit.Test;


public class ExecuteTest
{

    @Test
    public void test() throws Exception {
//        Execute.execute("ping localhost");
        Execute exe = new Execute(new PumpStreamHandler(), new ExecuteWatchdog(100));
        exe.setCommandline(new String[]{"ping" ,"localhost"});
        exe.execute();
    }

}
