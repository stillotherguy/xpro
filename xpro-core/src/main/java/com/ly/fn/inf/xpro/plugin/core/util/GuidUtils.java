package com.ly.fn.inf.xpro.plugin.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 * 
 * @version 1.0.0
 * @since 2016年1月20日 下午4:25:51
 */
public class GuidUtils {

	public static void main(String[] args) {
		System.out.println(System.nanoTime());
		System.out.println("UUID : " + genGuid());
	}

	public static String genGuid() {

		String uuidString = Long.toHexString(System.nanoTime() >> 2) + "-"
				+ Long.toHexString(System.nanoTime() >> 3) + "-"
				+ Long.toHexString(System.nanoTime() >> 4) + "-"
				+ Long.toHexString(System.nanoTime() >> 5) + "-"
				+ Long.toHexString(System.nanoTime() >> 6);
		String uuid = UUID.fromString(uuidString).toString();
		uuid = Thread.currentThread().getId()
				+ StringUtils.upperCase(StringUtils.replace(uuid, "-",
						StringUtils.EMPTY));
		return uuid;
	}
}
