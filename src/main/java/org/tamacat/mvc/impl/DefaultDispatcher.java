/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.Dispatcher;

public class DefaultDispatcher implements Dispatcher {

	static final Log LOG = LogFactory.getLog(DefaultDispatcher.class);

	protected String rootPath = "/WEB-INF/jsp";
	protected String extension = ".jsp";

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public void dispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = getPath(req.getRequestURI());
		LOG.debug("dispatch: " + path);
		RequestDispatcher dispatcher = req.getRequestDispatcher(path);
		dispatcher.forward(req, new DispatchHttpResponse(resp));
	}

	protected String getPath(String servletPath) {
		try {
			String path = servletPath.replace("..", "").replaceFirst("^/", "").toLowerCase();
			if (path.length() == 0) path = "index";
			if (path.endsWith("/")) path = path + "index";
			if (path.indexOf(".") == -1) {
				path = path + extension;
			} else if (path.endsWith(".html")) {
				path = path.replace(".html", extension);
			}
			
			Path paths = Paths.get(path);
			int count = paths.getNameCount();
			if (count >= 2) {
				String className = paths.getName(count-2).normalize().toString();
				String act = paths.getName(count-1).normalize().toString();
				path = className + "/"+act;
			}
			return rootPath + "/" + path;
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			return rootPath + "/error/404.jsp";
		}
	}
	
	protected class DispatchHttpResponse extends HttpServletResponseWrapper {

		public DispatchHttpResponse(HttpServletResponse response) {
			super(response);
		}
		
		@Override
		public void setContentType(String type) {
			//Skip JSP setContentType
		}
	}
}
