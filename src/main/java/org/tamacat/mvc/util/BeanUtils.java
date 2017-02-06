/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.orm.MapBasedORMappingBean;
import org.tamacat.dao.util.JSONUtils;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.CollectionUtils;

/**
 * Utility for Bind HttpServletRequest to Java Object (extends MapBasedORMappingBean)
 */
public class BeanUtils {

	static final String CONTENT_TYPE_JSON = "application/json";
	static final String JSON_ENCODING = "UTF-8";
	
	/**
	 * Bind request parameters to Object.
	 */
	public static <T extends MapBasedORMappingBean<T>> T bind(T bean, HttpServletRequest req, Column... columns) {
		for (Column column : columns) {
			bind(bean, req, column, column.getColumnName());
		}
		return bean;
	}

	/**
	 * Bind request parameters to Object.
	 */
	public static <T extends MapBasedORMappingBean<T>> T bind(T bean, HttpServletRequest req, Column column, String name) {
		String val = req.getParameter(name);
		if (val != null) {
			bean.val(column, val);
		}
		return bean;
	}
	
	/**
	 * Bind JSON request to Object (extends MapBasedORMappingBean)
	 * @sinse 1.4
	 * @return new Instance
	 */
	public static <T extends MapBasedORMappingBean<T>> T bindJson(HttpServletRequest req, Class<T> type, Column... columns) {
		return bindJson(ClassUtils.newInstance(type), req, columns);
	}
	
	/**
	 * Bind JSON request to Object (extends MapBasedORMappingBean)
	 * @since 1.4
	 */
	public static <T extends MapBasedORMappingBean<T>> T bindJson(T bean, HttpServletRequest req, Column... columns) {
		if (req.getContentType().startsWith(CONTENT_TYPE_JSON)) {
			Map<String, Column> colmaps = CollectionUtils.newLinkedHashMap();
			for (Column col : columns) {
				colmaps.put(col.getColumnName(), col);
			}
			try {
				req.setCharacterEncoding(JSON_ENCODING);
				bean.parseJson(req.getReader(), columns);
			} catch (IOException e) {
				//skip
			}
		}
		return bean;
	}
	
	/**
	 * Bind JSON request to Collection.
	 * @since 1.4
	 */
	public static <T extends MapBasedORMappingBean<T>> Collection<T> bindJsonArray(HttpServletRequest req, Class<T> type, Column... columns) {
		if (req.getContentType().startsWith(CONTENT_TYPE_JSON)) {
			Map<String, Column> colmaps = CollectionUtils.newLinkedHashMap();
			for (Column col : columns) {
				colmaps.put(col.getColumnName(), col);
			}
			try {
				req.setCharacterEncoding(JSON_ENCODING);
				return JSONUtils.parseArray(Json.createParser(req.getReader()), type, columns);
			} catch (IOException e) {
				//skip
			}
		}
		return CollectionUtils.newArrayList();
	}

}
