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
import org.tamacat.util.CollectionUtils;

public class BeanUtils {

	public static <T extends MapBasedORMappingBean<?>> MapBasedORMappingBean<?> bind(T bean, HttpServletRequest req, Column... columns) {
		for (Column column : columns) {
			String val = req.getParameter(column.getColumnName());
			if (val != null) {
				bean.val(column, val);
			}
		}
		return bean;
	}

	public static <T extends MapBasedORMappingBean<?>> MapBasedORMappingBean<?> bind(T bean, HttpServletRequest req, Column column, String name) {
		String val = req.getParameter(name);
		if (val != null) {
			bean.val(column, val);
		}
		return bean;
	}
	
	public static <T extends MapBasedORMappingBean<?>> MapBasedORMappingBean<?> bindJson(T bean, HttpServletRequest req, Column... columns) {
		if (req.getContentType().startsWith("application/json")) {
			Map<String, Column> colmaps = CollectionUtils.newLinkedHashMap();
			for (Column col : columns) {
				colmaps.put(col.getColumnName(), col);
			}
			try {
				bean.parseJson(req.getReader(), columns);
			} catch (IOException e) {
				//skip
			}
		}
		return bean;
	}
	
	public static <T extends MapBasedORMappingBean<?>> Collection<T> bindJsonArray(HttpServletRequest req, Class<T> type, Column... columns) {
		if (req.getContentType().startsWith("application/json")) {
			Map<String, Column> colmaps = CollectionUtils.newLinkedHashMap();
			for (Column col : columns) {
				colmaps.put(col.getColumnName(), col);
			}
			try {
				req.setCharacterEncoding("UTF-8");
				return JSONUtils.parseArray(Json.createParser(req.getReader()), type, columns);
			} catch (IOException e) {
				//skip
			}
		}
		return CollectionUtils.newArrayList();
	}

}
