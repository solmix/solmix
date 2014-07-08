/*
 *  Copyright 2012 The Solmix Project
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
package org.solmix.command.karaf.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.solmix.commons.util.IOUtils;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年12月13日
 */

public class DownLoadFileTest
{
    @Test
    public void test1(){
        
    }
//    @Test
    public void test(){
        try {
            InputStream is = new URL(null, "mvn:org.apache.karaf.shell/org.apache.karaf.shell.wrapper/2.3.3", new org.ops4j.pax.url.mvn.Handler()).openStream();
            File f = new File("/home/solmix/abc.jar");
            if (!f.exists())
                f.createNewFile();
            FileOutputStream fs = new FileOutputStream(f);
            IOUtils.copyStreams(is, fs);
            IOUtils.closeQuitely(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
