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
package org.solmix.test;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-10
 */

public class SolmixTestCase extends Assert
{
   /********************************************************************************   
        java.runtime.name=Java(TM) SE Runtime Environment, 
        sun.boot.library.path=/home/solmix/t/java/jdk1.6.0_45/jre/lib/i386, 
        java.vm.version=20.45-b01, 
        java.vm.vendor=Sun Microsystems Inc., 
        java.vendor.url=http://java.sun.com/, path.separator=:, 
        java.vm.name=Java HotSpot(TM) Server VM, 
        file.encoding.pkg=sun.io, 
        sun.java.launcher=SUN_STANDARD, 
        user.country=CN, 
        sun.os.patch.level=unknown, 
        java.vm.specification.name=Java Virtual Machine Specification, 
        user.dir=/home/solmix/o/EIAMS/eiams-commons, 
        java.runtime.version=1.6.0_45-b06, 
        java.awt.graphicsenv=sun.awt.X11GraphicsEnvironment, 
        java.endorsed.dirs=/home/solmix/t/java/jdk1.6.0_45/jre/lib/endorsed, 
        os.arch=i386, java.io.tmpdir=/tmp, line.separator=, 
        java.vm.specification.vendor=Sun Microsystems Inc., 
        os.name=Linux, 
        sun.jnu.encoding=UTF-8, 
        java.library.path=/home/solmix/t/java/jdk1.6.0_45/jre/lib/i386/server:/home/solmix/t/java/jdk1.6.0_45/jre/lib/i386:/home/solmix/t/java/jdk1.6.0_45/jre/../lib/i386:/home/solmix/t/java/jdk1.6.0_45/jre/lib/i386/client:/home/solmix/t/java/jdk1.6.0_45/jre/lib/i386::/usr/java/packages/lib/i386:/lib:/usr/lib, 
        java.specification.name=Java Platform API Specification, 
        java.class.version=50.0, 
        sun.management.compiler=HotSpot Tiered Compilers, 
        os.version=3.11.0-22-generic, 
        user.home=/home/solmix, 
        user.timezone=Asia/Manila, 
        java.awt.printerjob=sun.print.PSPrinterJob, 
        file.encoding=UTF-8, java.specification.version=1.6, 
        java.class.path=/home/solmix/o/EIAMS/eiams-commons/target/test-classes:/home/solmix/o/EIAMS/eiams-commons/target/classes:/home/solmix/o/git/solmix/framework/datasource/target/classes:/home/solmix/.m2/repository/commons-jxpath/commons-jxpath/1.3/commons-jxpath-1.3.jar:/home/solmix/.m2/repository/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.jar:/home/solmix/.m2/repository/commons-pool/commons-pool/1.5.4/commons-pool-1.5.4.jar:/home/solmix/.m2/repository/org/osgi/org.osgi.compendium/4.3.0/org.osgi.compendium-4.3.0.jar:/home/solmix/o/git/solmix/common/util/target/classes:/home/solmix/.m2/repository/org/slf4j/slf4j-api/1.7.2/slf4j-api-1.7.2.jar:/home/solmix/.m2/repository/org/apache/servicemix/bundles/org.apache.servicemix.bundles.oro/2.0.8_4/org.apache.servicemix.bundles.oro-2.0.8_4.jar:/home/solmix/.m2/repository/commons-codec/commons-codec/1.6/commons-codec-1.6.jar:/home/solmix/.m2/repository/org/apache/servicemix/bundles/org.apache.servicemix.bundles.commons-vfs/1.0_4/org.apache.servicemix.bundles.commons-vfs-1.0_4.jar:/home/solmix/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:/home/solmix/.m2/repository/org/osgi/org.osgi.core/4.3.0/org.osgi.core-4.3.0.jar:/home/solmix/o/git/solmix/framework/api/target/classes:/home/solmix/.m2/repository/org/apache/geronimo/specs/geronimo-servlet_2.5_spec/1.2/geronimo-servlet_2.5_spec-1.2.jar:/home/solmix/.m2/repository/org/apache/velocity/velocity/1.7/velocity-1.7.jar:/home/solmix/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar:/home/solmix/.m2/repository/commons-fileupload/commons-fileupload/1.3/commons-fileupload-1.3.jar:/home/solmix/.m2/repository/commons-io/commons-io/2.2/commons-io-2.2.jar:/home/solmix/.m2/repository/org/codehaus/jackson/jackson-xc/1.9.8/jackson-xc-1.9.8.jar:/home/solmix/.m2/repository/org/codehaus/jackson/jackson-core-asl/1.9.8/jackson-core-asl-1.9.8.jar:/home/solmix/.m2/repository/org/codehaus/jackson/jackson-mapper-asl/1.9.8/jackson-mapper-asl-1.9.8.jar:/home/solmix/o/git/solmix/common/runtime/target/classes:/home/solmix/o/git/solmix/framework/pool/target/classes:/home/solmix/o/git/solmix/framework/dsrepo/target/classes:/home/solmix/.m2/repository/org/javassist/javassist/3.18.1-GA/javassist-3.18.1-GA.jar:/home/solmix/o/git/solmix/modules/sql/target/classes:/home/solmix/o/git/solmix/modules/mybatis/target/classes:/home/solmix/.m2/repository/org/springframework/spring-core/3.2.4.RELEASE/spring-core-3.2.4.RELEASE.jar:/home/solmix/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:/home/solmix/.m2/repository/org/springframework/spring-extensions/3.2.4.RELEASE/spring-extensions-3.2.4.RELEASE.jar:/home/solmix/.m2/repository/org/springframework/spring-context/3.2.4.RELEASE/spring-context-3.2.4.RELEASE.jar:/home/solmix/.m2/repository/org/springframework/spring-aop/3.2.4.RELEASE/spring-aop-3.2.4.RELEASE.jar:/home/solmix/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar:/home/solmix/.m2/repository/org/springframework/spring-expression/3.2.4.RELEASE/spring-expression-3.2.4.RELEASE.jar:/home/solmix/.m2/repository/org/mybatis/mybatis/3.2.5/mybatis-3.2.5.jar:/home/solmix/.m2/repository/com/h2database/h2/1.3.172/h2-1.3.172.jar:/home/solmix/.m2/repository/junit/junit/4.7/junit-4.7.jar:/home/solmix/t/eclipse4.3/eclipse/configuration/org.eclipse.osgi/bundles/355/1/.cp/:/home/solmix/t/eclipse4.3/eclipse/configuration/org.eclipse.osgi/bundles/354/1/.cp/,
        user.name=solmix, java.vm.specification.version=1.0, 
        sun.java.command=org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 46522 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames com.ieslab.eiams.example.SqlOperationTest, 
        java.home=/home/solmix/t/java/jdk1.6.0_45/jre, 
        sun.arch.data.model=32, user.language=zh, 
        java.specification.vendor=Sun Microsystems Inc., 
        java.vm.info=mixed mode, java.version=1.6.0_45, 
        java.ext.dirs=/home/solmix/t/java/jdk1.6.0_45/jre/lib/ext:/usr/java/packages/lib/ext, 
        sun.boot.class.path=/home/solmix/t/java/jdk1.6.0_45/jre/lib/resources.jar:/home/solmix/t/java/jdk1.6.0_45/jre/lib/rt.jar:/home/solmix/t/java/jdk1.6.0_45/jre/lib/sunrsasign.jar:/home/solmix/t/java/jdk1.6.0_45/jre/lib/jsse.jar:/home/solmix/t/java/jdk1.6.0_45/jre/lib/jce.jar:/home/solmix/t/java/jdk1.6.0_45/jre/lib/charsets.jar:/home/solmix/t/java/jdk1.6.0_45/jre/lib/modules/jdk.boot.jar:/home/solmix/t/java/jdk1.6.0_45/jre/classes, 
        java.vendor=Sun Microsystems Inc., 
        solmix.base=/home/solmix/o/EIAMS/eiams-commons/target/test-classes/, 
        file.separator=/, 
        java.vendor.url.bug=http://java.sun.com/cgi-bin/bugreport.cgi, 
        sun.io.unicode.encoding=UnicodeLittle, 
        sun.cpu.endian=little, 
        sun.desktop=gnome, 
        sun.cpu.isalist=*/
    @Before
    public void init() {
        URL root= getClass().getResource("/");
       String rootPath= root.getPath();
       System.out.println("Used:"+rootPath+" As [solmix.base]");
       System.setProperty("solmix.base", rootPath);
    }
    @Test
    public void test(){
        
    }
}
