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
package org.solmix.commons.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * 配合cmmons FileUtils使用
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月14日
 */

public class Files {
	/** URL prefix for loading from the file system: "file:" */
	public static final String FILE_URL_PREFIX = "file:";

	/** URL protocol for a file in the file system: "file" */
	public static final String URL_PROTOCOL_FILE = "file";

	/** URL protocol for an entry from a jar file: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** URL protocol for an entry from a zip file: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a JBoss jar file: "vfszip" */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** URL protocol for a JBoss file system resource: "vfsfile" */
	public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

	/** URL protocol for a general JBoss VFS resource: "vfs" */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** Separator between JAR URL and file path within the JAR */
	public static final String JAR_URL_SEPARATOR = "!/";
	
	private static final String FOLDER_SEPARATOR = "/";

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	public static final String CLASSPATH_URL_PREFIX ="classpath:";
	/**
     * 获取文件MD5值
     *
     * @param file
     * @return
     */
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
     
    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static long getFileLength(File file)
            throws IOException {
        FileInputStream fis = null;
        fis = new FileInputStream(file);
        
        long size= fis.available();
        IOUtils.closeQuietly(fis);
        return size;
    }
     
    /**
     * 读取文件到二进制
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file)
            throws IOException {
        InputStream is = new FileInputStream(file);
         
        long length = file.length();
         
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
         
        byte[] bytes = new byte[(int) length];
         
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
         
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("不能读取文件: " + file.getName());
        }
         
        is.close();
        return bytes;
    }
     
    /**
     * 获取标准文件大小，如30KB，15.5MB
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileSize(File file)
            throws IOException {
        long size = getFileLength(file);
        DecimalFormat df = new DecimalFormat("###.##");
        float f;
        if (size < 1024 * 1024) {
            f = (float) size / (float) 1024;
            return (df.format(new Float(f).doubleValue()) + " KB");
        } else {
            f = (float) size / (float) (1024 * 1024);
            return (df.format(new Float(f).doubleValue()) + " MB");
        }
         
    }
     
    /**
     * 复制文件
     *
     * @param f1
     *            源文件
     * @param f2
     *            目标文件
     * @throws Exception
     */
    public static void copyFile(File f1, File f2)
            throws Exception {
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        ByteBuffer b = null;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
            }
            if ((inC.size() - inC.position()) < length) {
                length = (int) (inC.size() - inC.position());
            } else
                length = 2097152;
            b = ByteBuffer.allocateDirect(length);
            inC.read(b);
            b.flip();
            outC.write(b);
            outC.force(false);
        }
    }
     
    /**
     * 检查文件是否存在
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static boolean existFile(String fileName)
            throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("文件未找到:" + fileName);
        }
        return file.exists();
    }
     
    /**
     * 删除文件
     *
     * @param fileName
     */
    public static void deleteFile(String fileName)
            throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("文件未找到:" + fileName);
        }
        file.delete();
    }
     
    /**
     * 读取文件到字符串
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName)
            throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("文件未找到:" + fileName);
        }
         
        BufferedReader in = new BufferedReader(new FileReader(file));
        StringBuffer sb = new StringBuffer();
        String str = "";
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        return sb.toString();
    }
     
    /**
     * 获取目录所有所有文件和文件夹
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<File> listFiles(String fileName)
            throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IOException("文件未找到:" + fileName);
        }
        return Arrays.asList(file.listFiles());
    }
     
    /**
     * 创建目录
     *
     * @param dir
     */
    public static void mkdir(String dir) {
        String dirTemp = dir;
        File dirPath = new File(dirTemp);
        if (!dirPath.exists()) {
            dirPath.mkdir();
        }
    }
     
    /**
     * 新建文件
     *
     * @param fileName
     *            String 包含路径的文件名 如:E:\phsftp\src\123.txt
     * @param content
     *            String 文件内容
     */
    public static void createNewFile(String fileName, String content)
            throws IOException {
        String fileNameTemp = fileName;
        File filePath = new File(fileNameTemp);
        if (!filePath.exists()) {
            filePath.createNewFile();
        }
        FileWriter fw = new FileWriter(filePath);
        PrintWriter pw = new PrintWriter(fw);
        String strContent = content;
        pw.println(strContent);
        pw.flush();
        pw.close();
        fw.close();
         
    }
     
    /**
     * 删除文件夹
     *
     * @param folderPath
     *            文件夹路径
     */
    public static void delFolder(String folderPath) {
        // 删除文件夹里面所有内容
        delAllFile(folderPath);
        String filePath = folderPath;
        java.io.File myFilePath = new java.io.File(filePath);
        // 删除空文件夹
        myFilePath.delete();
    }
     
    /**
     * 删除文件夹里面的所有文件
     *
     * @param path
     *            文件夹路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] childFiles = file.list();
        File temp = null;
        for (int i = 0; i < childFiles.length; i++) {
            // File.separator与系统有关的默认名称分隔符
            // 在UNIX系统上，此字段的值为'/'；在Microsoft Windows系统上，它为 '\'。
            if (path.endsWith(File.separator)) {
                temp = new File(path + childFiles[i]);
            } else {
                temp = new File(path + File.separator + childFiles[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separatorChar + childFiles[i]);// 先删除文件夹里面的文件
                delFolder(path + File.separatorChar + childFiles[i]);// 再删除空文件夹
            }
        }
    }
     
    /**
     * 复制单个文件，传统方式
     *
     * @param srcFile
     *            包含路径的源文件 如：E:/phsftp/src/abc.txt
     * @param dirDest
     *            目标文件目录；若文件目录不存在则自动创建 如：E:/phsftp/dest
     * @throws IOException
     */
    public static void copyFile(String srcFile, String dirDest)
            throws IOException {
        FileInputStream in = new FileInputStream(srcFile);
        mkdir(dirDest);
        FileOutputStream out = new FileOutputStream(dirDest + "/" + new File(srcFile).getName());
        int len;
        byte buffer[] = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();
    }
     
    /**
     * 复制文件夹
     *
     * @param oldPath
     *            String 源文件夹路径 如：E:/phsftp/src
     * @param newPath
     *            String 目标文件夹路径 如：E:/phsftp/dest
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath)
            throws IOException {
        // 如果文件夹不存在 则新建文件夹
        mkdir(newPath);
        File file = new File(oldPath);
        String[] files = file.list();
        File temp = null;
        for (int i = 0; i < files.length; i++) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + files[i]);
            } else {
                temp = new File(oldPath + File.separator + files[i]);
            }
             
            if (temp.isFile()) {
                FileInputStream input = new FileInputStream(temp);
                FileOutputStream output = new FileOutputStream(newPath + "/"
                        + (temp.getName()).toString());
                byte[] buffer = new byte[1024 * 2];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
            }
        }
    }
     
    /**
     * 移动文件到指定目录
     *
     * @param oldPath
     *            包含路径的文件名 如：E:/phsftp/src/ljq.txt
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFile(String oldPath, String newPath)
            throws IOException {
        copyFile(oldPath, newPath);
        deleteFile(oldPath);
    }
     
    /**
     * 移动文件到指定目录，不会删除文件夹
     *
     * @param oldPath
     *            源文件目录 如：E:/phsftp/src
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFiles(String oldPath, String newPath)
            throws IOException {
        copyFolder(oldPath, newPath);
        delAllFile(oldPath);
    }
     
    /**
     * 移动文件到指定目录，会删除文件夹
     *
     * @param oldPath
     *            源文件目录 如：E:/phsftp/src
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFolder(String oldPath, String newPath)
            throws IOException {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }
     
    /**
     * 解压zip文件
     * 说明:本程序通过ZipOutputStream和ZipInputStream实现了zip压缩和解压功能.
     * 问题:由于java.util.zip包并不支持汉字,当zip文件中有名字为中文的文件时,
     * 就会出现异常:"Exception  in thread "main " java.lang.IllegalArgumentException 
     * at java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:285)
     * @param srcDir
     *            解压前存放的目录
     * @param destDir
     *            解压后存放的目录
     * @throws Exception
     */
    public static void unZip(String srcDir, String destDir)
            throws IOException {
        int leng = 0;
        byte[] b = new byte[1024 * 2];
        /** 获取zip格式的文件 **/
        File[] zipFiles = new ExtensionFileFilter("zip").getFiles(srcDir);
        if (zipFiles != null && !"".equals(zipFiles)) {
            for (int i = 0; i < zipFiles.length; i++) {
                File file = zipFiles[i];
                /** 解压的输入流 * */
                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
                ZipEntry entry = null;
                while ((entry = zis.getNextEntry()) != null) {
                    File destFile = null;
                    if (destDir.endsWith(File.separator)) {
                        destFile = new File(destDir + entry.getName());
                    } else {
                        destFile = new File(destDir + File.separator + entry.getName());
                    }
                    /** 把解压包中的文件拷贝到目标目录 * */
                    FileOutputStream fos = new FileOutputStream(destFile);
                    while ((leng = zis.read(b)) != -1) {
                        fos.write(b, 0, leng);
                    }
                    fos.close();
                }
                zis.close();
            }
        }
    }
     
    /**
     * 压缩文件
     * 说明:本程序通过ZipOutputStream和ZipInputStream实现了zip压缩和解压功能.
     * 问题:由于java.util.zip包并不支持汉字,当zip文件中有名字为中文的文件时,
     * 就会出现异常:"Exception  in thread "main " java.lang.IllegalArgumentException 
     * at java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:285)
     * @param srcDir
     *            压缩前存放的目录
     * @param destDir
     *            压缩后存放的目录
     * @throws Exception
     */
    public static void zip(String srcDir, String destDir)
            throws IOException {
        String tempFileName = null;
        byte[] buf = new byte[1024 * 2];
        int len;
        // 获取要压缩的文件
        File[] files = new File(srcDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    if (destDir.endsWith(File.separator)) {
                        tempFileName = destDir + file.getName() + ".zip";
                    } else {
                        tempFileName = destDir + File.separator + file.getName() + ".zip";
                    }
                    FileOutputStream fos = new FileOutputStream(tempFileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    ZipOutputStream zos = new ZipOutputStream(bos);// 压缩包
                     
                    ZipEntry ze = new ZipEntry(file.getName());// 压缩包文件名
                    zos.putNextEntry(ze);// 写入新的ZIP文件条目并将流定位到条目数据的开始处
                     
                    while ((len = bis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                        zos.flush();
                    }
                    bis.close();
                    zos.close();
                     
                }
            }
        }
    }
     
    /**
     * 读取数据
     *
     * @param inSream
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String readData(InputStream inSream, String charsetName)
            throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }
     
    /**
     * 一行一行读取文件，适合字符读取，若读取中文字符时会出现乱码
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static Set<String> readFileLine(String path)
            throws IOException {
        Set<String> datas = new HashSet<String>();
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            datas.add(line);
        }
        br.close();
        fr.close();
        return datas;
    }
     
    public static void main(String[] args) {
        try {
            unZip("c:/test", "c:/test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String normalizeAbsolutePath(String path, boolean removeTrailingSlash) throws IllegalPathException {
        return normalizePath(path, true, false, removeTrailingSlash);
    }

    private static String normalizePath(String path, boolean forceAbsolute,
        boolean forceRelative, boolean removeTrailingSlash)
        throws IllegalPathException {
        char[] pathChars = StringUtils.trimToEmpty(path).toCharArray();
        int length = pathChars.length;

        // 检查绝对路径，以及path尾部的"/"
        boolean startsWithSlash = false;
        boolean endsWithSlash = false;

        if (length > 0) {
            char firstChar = pathChars[0];
            char lastChar = pathChars[length - 1];

            startsWithSlash = firstChar == '/' || firstChar == '\\';
            endsWithSlash = lastChar == '/' || lastChar == '\\';
        }

        StringBuilder buf = new StringBuilder(length);
        boolean isAbsolutePath = forceAbsolute || !forceRelative
            && startsWithSlash;
        int index = startsWithSlash ? 0 : -1;
        int level = 0;

        if (isAbsolutePath) {
            buf.append("/");
        }

        while (index < length) {
            // 跳到第一个非slash字符，或末尾
            index = indexOfSlash(pathChars, index + 1, false);

            if (index == length) {
                break;
            }

            // 取得下一个slash index，或末尾
            int nextSlashIndex = indexOfSlash(pathChars, index, true);

            String element = new String(pathChars, index, nextSlashIndex
                - index);
            index = nextSlashIndex;

            // 忽略"."
            if (".".equals(element)) {
                continue;
            }

            // 回朔".."
            if ("..".equals(element)) {
                if (level == 0) {
                    // 如果是绝对路径，../试图越过最上层目录，这是不可能的，
                    // 抛出路径非法的异常。
                    if (isAbsolutePath) {
                        throw new IllegalPathException(path);
                    } else {
                        buf.append("../");
                    }
                } else {
                    buf.setLength(pathChars[--level]);
                }

                continue;
            }

            // 添加到path
            pathChars[level++] = (char) buf.length(); // 将已经读过的chars空间用于记录指定level的index
            buf.append(element).append('/');
        }

        // 除去最后的"/"
        if (buf.length() > 0) {
            if (!endsWithSlash || removeTrailingSlash) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    private static int indexOfSlash(char[] chars, int beginIndex, boolean slash) {
        int i = beginIndex;

        for (; i < chars.length; i++) {
            char ch = chars[i];

            if (slash) {
                if (ch == '/' || ch == '\\') {
                    break; // if a slash
                }
            } else {
                if (ch != '/' && ch != '\\') {
                    break; // if not a slash
                }
            }
        }

        return i;
    }
	public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);
			try {
				return new URL(jarFile);
			}
			catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
				// This usually indicates that the jar file resides in the file system.
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}
				return new URL(FILE_URL_PREFIX + jarFile);
			}
		}
		else {
			return jarUrl;
		}
	}

	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol) ||
				URL_PROTOCOL_VFSZIP.equals(protocol) );
	}

	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		Assert.assertNotNull(resourceUrl, "Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUrl);
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		}
		catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}
	
	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
	    Assert.assertNotNull(resourceUri, "Resource URI must not be null");
          if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
                throw new FileNotFoundException(
                            description + " cannot be resolved to absolute file path " +
                            "because it does not reside in the file system: " + resourceUri);
          }
          return new File(resourceUri.getSchemeSpecificPart());
    }
	
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}
	
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	public static boolean isFileURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
				URL_PROTOCOL_VFS.equals(protocol));
	}

	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
              String newPath = path.substring(0, separatorIndex);
              if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                    newPath += FOLDER_SEPARATOR;
              }
              return newPath + relativePath;
        }
        else {
              return relativePath;
        }
    }
}
 
 
class ExtensionFileFilter  implements FileFilter {
     
    private final String extension;
     
    public ExtensionFileFilter(String extension) {
        this.extension = extension;
    }
     
    public File[] getFiles(String srcDir) throws IOException {
        return (File[]) Files.listFiles(srcDir).toArray();
    }
     
    @Override
	public boolean accept(File file) {
        if (file.isDirectory()) {
            return false;
        }
         
        String name = file.getName();
        // find the last
        int idx = name.lastIndexOf(".");
        if (idx == -1) {
            return false;
        } else if (idx == name.length() - 1) {
            return false;
        } else {
            return this.extension.equals(name.substring(idx + 1));
        }
    }
     
}
