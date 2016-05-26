/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.exec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年4月19日
 */

public class Background {
	public static void exec(String[] cmd) throws IOException {
		File devNull;
		if (Os.isFamily("unix"))
			devNull = new File("/dev/null");
		else if (Os.isFamily("windows"))
			devNull = new File("NUL");
		else
			throw new IllegalStateException("Unhandled Java environment");
		exec(cmd, devNull, false, devNull, false);
	}

	/**
	 * Execute a command (and its args, ala Runtime.exec)
	 *
	 * @param outFile
	 *            File to send standard out from the process to
	 * @param appendOut
	 *            If true, append the file with standard out, else truncate or
	 *            create a new file
	 * @param errFile
	 *            File to send standard err from the process to
	 * @param appendErr
	 *            If true, append the file with standard error, else truncate or
	 *            create a new file
	 */
	public static void exec(String[] cmd, File outFile, boolean appendOut,
			File errFile, boolean appendErr) throws IOException {
		if (Os.isFamily("unix"))
			execUnix(cmd, outFile, appendOut, errFile, appendErr);
		else if (Os.isFamily("windows"))
			execWin(cmd, outFile, appendOut, errFile, appendErr);
		else
			throw new IllegalStateException("Unhandled Java environment");
	}

	private static void execUnix(String[] cmd, File outFile, boolean appendOut,
			File errFile, boolean appendErr) throws IOException {
		StringBuffer escaped;
		String[] execCmd;
		Runtime r;

		escaped = new StringBuffer();
		for (int i = 0; i < cmd.length; i++) {
			escaped.append(Escape.escape(cmd[i]));
			escaped.append(" ");
		}

		execCmd = new String[] {
				"/bin/sh",
				"-c",
				escaped.toString() + (appendOut == true ? ">>" : ">")
						+ Escape.escape(outFile.getAbsolutePath()) + " 2"
						+ (appendErr == true ? ">>" : " >")
						+ Escape.escape(errFile.getAbsolutePath())
						+ " </dev/null &" };

		Process p = Runtime.getRuntime().exec(execCmd);
		try {
			p.waitFor();
		} catch (Exception exc) {
			throw new IOException("Unable to properly background process: "
					+ exc.getMessage());
		}
	}

	private static void execWin(String[] cmd, File outFile, boolean appendOut,
			File errFile, boolean appendErr) throws IOException {
		String[] logargs;
		String[] execCmd;
		ArrayList tmpCmd = new ArrayList();
		Runtime r;

		tmpCmd.add("cmd");
		tmpCmd.add("/c");
		tmpCmd.add("start");
		tmpCmd.add("/b");
		tmpCmd.add("\"\"");
		tmpCmd.add("/MIN");
		for (int i = 0; i < cmd.length; i++) {
			tmpCmd.add(cmd[i]);
		}
		tmpCmd.add((appendOut == true ? ">>" : ">")
				+ Escape.escape(outFile.getAbsolutePath()));
		tmpCmd.add((outFile.equals(errFile) ? " 2&" : " 2")
				+ (appendErr == true ? ">>" : " >")
				+ Escape.escape(errFile.getAbsolutePath()));

		Process p = Runtime.getRuntime().exec((String[]) tmpCmd.toArray(cmd));
	}

	public static void main(String[] args) throws Exception {
		Background.exec(new String[] { "javaq", "foo bar", "bar" }, new File(
				"garfo"), true, new File("barfo"), true);
	}
}
