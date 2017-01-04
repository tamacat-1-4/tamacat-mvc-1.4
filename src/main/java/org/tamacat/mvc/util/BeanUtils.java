/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.util;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.orm.ORMappingSupport;
import org.tamacat.util.CollectionUtils;

public class BeanUtils {

	public static <T> ORMappingSupport<? extends T> bind(ORMappingSupport<? extends T> bean, HttpServletRequest req, Column... columns) {
		for (Column column : columns) {
			String val = req.getParameter(column.getColumnName());
			if (val != null) {
				bean.val(column, val);
			}
		}
		return bean;
	}

	public static <T> ORMappingSupport<? extends T> bind(ORMappingSupport<? extends T> bean, HttpServletRequest req, Column column, String name) {
		String val = req.getParameter(name);
		if (val != null) {
			bean.val(column, val);
		}
		return bean;
	}
	
	public static <T> ORMappingSupport<? extends T> bindJson(ORMappingSupport<? extends T> bean, HttpServletRequest req, Column... columns) {
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

}
