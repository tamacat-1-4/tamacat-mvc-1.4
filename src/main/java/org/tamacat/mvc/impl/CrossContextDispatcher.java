/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class CrossContextDispatcher extends DefaultDispatcher {

	static final Log LOG = LogFactory.getLog(CrossContextDispatcher.class);
	protected String context; // "/portal"

	@Override
	public void dispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (context != null) {
			String path = getPath(req.getRequestURI());
			LOG.debug("dispatch: " + path);
			RequestDispatcher dispatcher = req.getServletContext().getContext(context).getRequestDispatcher(path);
			dispatcher.forward(req, new DispatchHttpResponse(resp));
		} else {
			super.dispatcher(req, resp);
		}
	}
}
