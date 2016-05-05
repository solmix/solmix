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
package org.solmix.runtime.security;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

/**
 * 加密/解密工具类
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年4月15日
 */

public final class SecurityUtils {
	/** 加密算法 */
	public final static String DEFAULT_ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";
	/** 加密标示前缀 */
	public final static String ENC_MARK_PREFIX = "ENC(";
	/** 加密标示后缀 */
	public final static String ENC_MARK_POSTFIX = ")";

	/**
	 * 字符串是否带加密标示
	 * @param str 被检查文本
	 * @return
	 */
	public static boolean isMarkedEncrypted(String str) {
		if (str == null) {
			return false;
		}
		String uStr = str.toUpperCase();
		return uStr.startsWith(ENC_MARK_PREFIX) && uStr.endsWith(ENC_MARK_POSTFIX);
	}

	/**
	 * 去除加密标示
	 * 
	 * @param str 带加密标示的字符串
	 * @return
	 */
	public static String unmark(String str) {
		return str.substring(ENC_MARK_PREFIX.length(), str.length() - ENC_MARK_POSTFIX.length());
	}

	/**
	 * 循环去除所有加密标示
	 * 
	 * @param str
	 * @return
	 */
	public static String unmarkRecursive(String str) {

		while (str.startsWith(ENC_MARK_PREFIX)) {
			str = str.substring(ENC_MARK_PREFIX.length(), str.length() - ENC_MARK_POSTFIX.length());
		}

		return str;
	}

	/**
	 * 添加加密标示
	 * 
	 * @param str
	 * @return
	 */
	public static String mark(String str) {
		return new StringBuilder().append(ENC_MARK_PREFIX).append(str).append(ENC_MARK_POSTFIX).toString();
	}

	/**
	 * 默认加密器,PBEWithMD5AndDES
	 * @param pbePass 密码
	 * @return
	 */
	public static StandardPBEStringEncryptor getStandardPBEStringEncryptor(String pbePass) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(DEFAULT_ENCRYPTION_ALGORITHM);
		encryptor.setPassword(pbePass);
		return encryptor;
	}

	/**
	 * 加密字符串
	 * 
	 * @param encryptor
	 *            initialized encryptor
	 * @param data
	 * @return
	 */
	public static String encrypt(StringEncryptor encryptor, String data) {
		return PropertyValueEncryptionUtils.encrypt(data, encryptor);
	}

	/**
	 *   加密字符串
	 * @param encryptionAlgorithm 加密算法
	 * @param encryptionKey 密码
	 * @param data 需加密文本
	 * @return
	 */
	public static String encrypt(String encryptionAlgorithm, String encryptionKey, String data) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(encryptionKey);
		encryptor.setAlgorithm(encryptionAlgorithm);
		return encrypt(encryptor, data);
	}

	/**
	 * 自动识别加密标示,并解密
	 * @param encryptor
	 * @param data
	 * @return
	 */
	public static String decryptRecursiveUnmark(StringEncryptor encryptor, String data) {
		return encryptor.decrypt(unmarkRecursive(data.trim()));
	}

	/**
	 * 解密
	 * 
	 * @param encryptor
	 * @param data
	 * @return
	 */
	public static String decrypt(StringEncryptor encryptor, String data) {
		return PropertyValueEncryptionUtils.decrypt(data, encryptor);
	}

	/**
	 * 根据加密算法和密码解密
	 * 
	 * @param encryptor
	 *            initialized encryptor
	 * @param data
	 * @return
	 */
	public static String decrypt(String encryptionAlgorithm, String encryptionKey, String data) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(encryptionKey);
		encryptor.setAlgorithm(encryptionAlgorithm);
		return decrypt(encryptor, data);
	}
}
