/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.util;

import javax.servlet.http.HttpServletRequest;

import org.tamacat.dao.meta.Column;
import org.tamacat.dao.orm.ORMappingSupport;

public class BeanUtils {

	public static ORMappingSupport bind(ORMappingSupport bean, HttpServletRequest req, Column... columns) {
		for (Column column : columns) {
			String val = req.getParameter(column.getColumnName());
			if (val != null) {
				bean.setValue(column, val);
			}
		}
		return bean;
	}

	public static ORMappingSupport bind(ORMappingSupport bean, HttpServletRequest req, Column column, String name) {
		String val = req.getParameter(name);
		if (val != null) {
			bean.setValue(column, val);
		}
		return bean;
	}
}
