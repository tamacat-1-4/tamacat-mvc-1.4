/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.di.DIContainerException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.Controller;
import org.tamacat.mvc.error.NotFoundException;

//@WebServlet(name = "StandardServlet", urlPatterns = { "/app/*" }, 
//initParams = { @WebInitParam(name = "controller", value = "controller.xml") })
public class StandardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	static final Log LOG = LogFactory.getLog(StandardServlet.class);
	protected DIContainer di;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String xml = getInitParameter("controller");
		if (xml == null) xml = "controller.xml";
		di = DI.configure(xml);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	protected void process(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("contextPath", req.getContextPath());
		Controller controller = getController(getServletName());
		try {
			controller.handleRequest(req, resp);
			if (resp.isCommitted() == false) {
				controller.dispatcher(req, resp);
			}
		} catch (Exception e) {
			controller.handleException(req, resp, e);
		}
	}

	protected Controller getController(String id) {
		try {
			return di.getBean(id, Controller.class);
		} catch (DIContainerException e) {
			throw new NotFoundException(null, e);
		}
	}
}
