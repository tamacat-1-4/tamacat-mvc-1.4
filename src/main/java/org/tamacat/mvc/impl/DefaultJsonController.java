/*
 * Copyright (c) 2016 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.mvc.Controller;
import org.tamacat.mvc.ExceptionHandler;
import org.tamacat.mvc.RequestHandler;

public class DefaultJsonController implements Controller {

	protected RequestHandler requestHandler = new ActionHandler();
	protected ExceptionHandler exceptionHandler = new StatusExceptionHandler();

	@Override
	public void setHandler(Object handler) {
		if (handler instanceof RequestHandler) {
			requestHandler = (RequestHandler) handler;
		}
		if (handler instanceof ExceptionHandler) {
			exceptionHandler = (ExceptionHandler) handler;
		}
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("application/json; charset=UTF-8");
		requestHandler.handleRequest(req, resp);
	}

	@Override
	public void handleException(HttpServletRequest req,
			HttpServletResponse resp, Exception e) {
		exceptionHandler.handleException(req, resp, e);
	}

	@Override
	public void dispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}
}
