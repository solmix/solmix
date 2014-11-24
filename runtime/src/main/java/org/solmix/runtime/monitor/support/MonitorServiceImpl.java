/**
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

package org.solmix.runtime.monitor.support;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;

import org.solmix.runtime.bean.Configurable;
import org.solmix.runtime.monitor.MonitorInfo;
import org.solmix.runtime.monitor.MonitorService;

import com.sun.management.OperatingSystemMXBean;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月15日
 */

@SuppressWarnings("restriction")
public class MonitorServiceImpl implements MonitorService, Configurable {

    private static final int CPUTIME = 5000;

    private static final int PERCENT = 100;

    private static final int FAULTLENGTH = 10;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.monitor.MonitorService#getMonitorInfo()
     */
    @Override
    public MonitorInfo getMonitorInfo() {
        int kb = 1024;

        // 可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / kb;
        // 剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / kb;
        // 最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / kb;

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // 操作系统
        String osName = System.getProperty("os.name");
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        // 已使用的物理内存
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize())
            / kb;

        // 获得线程总数
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); 
            parentThread.getParent() != null; 
            parentThread = parentThread.getParent()) ;
        int totalThread = parentThread.activeCount();

        double cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = this.getCpuRatioForWindows();
        }

        MonitorInfo infoBean = new MonitorInfo();
        infoBean.setFreeMemory(freeMemory);
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        infoBean.setMaxMemory(maxMemory);
        infoBean.setOsName(osName);
        infoBean.setTotalMemory(totalMemory);
        infoBean.setTotalMemorySize(totalMemorySize);
        infoBean.setTotalThread(totalThread);
        infoBean.setUsedMemory(usedMemory);
        infoBean.setCpuRatio(cpuRatio);
        return infoBean;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.Configurable#getConfigueName()
     */
    @Override
    public String getConfigueName() {
        return MonitorServiceImpl.class.getName();
    }

    private double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                + "//system32//wbem//wmic.exe process get Caption,CommandLine,"
                + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return Double.valueOf(
                    PERCENT * (busytime) / (busytime + idletime)).doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /** */
    /**
     * 读取CPU信息.
     * 
     * @param proc
     * @return
     * @author amg * Creation date: 2008-4-25 - 下午06:10:14
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = substring(line, capidx, cmdidx - 1).trim();
                String cmd = substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                // LOG.info("line="+line);
                if (caption.equals("System Idle Process")
                    || caption.equals("System")) {
                    idletime += Long.valueOf(
                        substring(line, kmtidx, rocidx - 1).trim()).longValue();
                    idletime += Long.valueOf(
                        substring(line, umtidx, wocidx - 1).trim()).longValue();
                    continue;
                }

                kneltime += Long.valueOf(
                    substring(line, kmtidx, rocidx - 1).trim()).longValue();
                usertime += Long.valueOf(
                    substring(line, umtidx, wocidx - 1).trim()).longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new long[] {};
    }

    public static String substring(final String src,final int startIdx,final int endIdx) {
        byte[] b = src.getBytes();
        String tgt = "";
        for (int i = startIdx; i <= endIdx; i++) {
            tgt += (char) b[i];
        }
        return tgt;
    }
}
