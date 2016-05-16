/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.mvc.action.ActionDefine;
import org.tamacat.mvc.error.InvalidRequestException;
import org.tamacat.util.StringUtils;

public class ServletUtils {

	public static <T>T getParameter(HttpServletRequest req, String name, T defaultValue) {
		return StringUtils.parse(req.getParameter(name), defaultValue);
	}
	
	public static <T>T param(HttpServletRequest req, String name, T defaultValue) {
		T val = StringUtils.parse(req.getParameter(name), defaultValue);
		req.setAttribute(name, val);
		return val;
	}
	
	public static String getCookie(HttpServletRequest req, String key) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (key.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	public static String getHeader(HttpServletRequest req, String key, String defaultValue) {
		String value = req.getHeader(key);
		if (value == null) {
			if (defaultValue != null) {
				value = defaultValue;
			} else {
				return null;
			}
		}
		return value.replace("\r", "").replace("\n", "");
	}

	public static void setHeader(HttpServletResponse resp, String key,
			String value) {
		if (value != null) {
			resp.setHeader(key, value.replace("\r", "").replace("\n", ""));
		}
	}

	public static void setContentDisposition(HttpServletResponse resp,
			String disposition, String filename) {
		setHeader(resp, "Content-Disposition", disposition+"; filename="+filename);
	}

	/**
	 * Set a Content-Disposition Response Header for File Download.(with URL Encoding)
	 * @param resp
	 * @param disposition
	 * @param filename
	 * @param charset
	 * @sinse 1.4-20160516
	 */
	public static void setContentDisposition(HttpServletResponse resp,
			String disposition, String filename, String charset) {
		setHeader(resp, "Content-Disposition", disposition+"; filename*="+charset+"''"+urlencode(filename, charset));
	}

	public static void bufferedWrite(HttpServletResponse resp, InputStream in)
			throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		byte[] buf = new byte[8192];
		OutputStream out = resp.getOutputStream();
		int len;
		while ((len = bin.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		bin.close();
	}

	public static PrintWriter getWriter(HttpServletResponse resp) {
		try {
			return resp.getWriter();
		} catch (IOException e) {
			return new PrintWriter(System.out);
		}
	}
	
	public static void println(HttpServletResponse resp, String val) {
		PrintWriter out = getWriter(resp);
		out.println(val);
		out.flush();
	}
	
	public static void setActionDefine(HttpServletRequest req,
			ActionDefine actionDef) {
		req.setAttribute("ActionProcessor.ActionDefine", actionDef);
	}

	public static ActionDefine getActionDefine(HttpServletRequest req) {
		return (ActionDefine) req.getAttribute("ActionProcessor.ActionDefine");
	}
	
	/**
	 * URL Encode UTF-8 (*,-,+ support)
	 * @param value target String
	 * @since 1.3
	 */
	public static String urlencode(String value) {
		return urlencode(value, "UTF-8");
	}
	
	/**
	 * URL Encode (*,-,+ support)
	 * @param value target String
	 * @param enc Encode
	 * @since 1.3
	 */
	public static String urlencode(String value, String enc) {
		try {
			return URLEncoder.encode(value, enc)
				.replace("*", "%2a").replace("-", "%2d").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new InvalidRequestException(e);
		}
	}
}
