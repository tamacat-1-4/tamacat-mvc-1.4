/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.util;

import java.util.Properties;

import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

/**
 * Properties file in CLASSPATH
 * - org/tamacat/mvc/mime-types.properties
 * - mime-types.properties
 * hash data (key:file extention, value:content-type)
 */
public class MimeUtils {
	private static Properties mimeTypes;

	static {
		mimeTypes = PropertyUtils.marge(
				"org/tamacat/mvc/mime-types.properties",
				"mime-types.properties");
	}

	/**
	 * Get a content-type from mime-types.properties.
	 * content-type was unknown then returns null.
	 * @param path
	 * @return
	 */
	public static String getContentType(String path) {
		if (StringUtils.isEmpty(path)) return null;
		String ext = path.substring(path.lastIndexOf('.') + 1, path.length());
		String contentType = mimeTypes.getProperty(ext.toLowerCase());
		return contentType;
	}
}
