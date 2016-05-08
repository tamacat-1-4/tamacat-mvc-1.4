/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.servlet.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MockServletConfig implements ServletConfig {

	protected Map<String, String> initParams = new HashMap<>();

	protected String servletName;
	ServletContext context;

	public MockServletConfig(String servletName) {
		this.servletName = servletName;
	}

	@Override
	public String getServletName() {
		return servletName;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public String getInitParameter(String name) {
		return initParams.get(name);
	}

	public void setInitParam(String key, String value) {
		initParams.put(key, value);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		final Iterator<String> ite = initParams.keySet().iterator();
		return new Enumeration<String>() {

			public boolean hasMoreElements() {
				return ite.hasNext();
			}

			public String nextElement() {
				return ite.next();
			}
		};
	}

}
