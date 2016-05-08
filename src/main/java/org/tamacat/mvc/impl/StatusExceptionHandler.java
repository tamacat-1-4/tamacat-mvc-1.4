/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.impl;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.ExceptionHandler;
import org.tamacat.mvc.error.AjaxActionException;
import org.tamacat.mvc.error.ClientSideException;
import org.tamacat.mvc.error.HttpStatusException;
import org.tamacat.mvc.error.InternalServerErrorException;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.StringUtils;

public class StatusExceptionHandler implements ExceptionHandler {

	static final Log LOG = LogFactory.getLog(ExceptionHandler.class);
	static final Log TRACE = LogFactory.getLog(StatusExceptionHandler.class);

	protected String errorPagePath = "/WEB-INF/jsp/error";
	protected String extension = ".jsp";
	
	/**
	 * Set the directory of error page. default value is "/WEB-INF/jsp/error"
	 * @param errorPagePath
	 */
	public void setErrorPagePath(String errorPagePath) {
		this.errorPagePath = errorPagePath;
	}

	/**
	 * Set extension.
	 * @param extension
	 * @since 1.3
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	@Override
	public void handleException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		if (e != null) {
			int status = 500;
			String message = "Internal Server Error";
			if (e instanceof ClientSideException) {
				message = e.getMessage();
			}
			req.setAttribute("error_message", message);
			
			Throwable cause = e.getCause();
			if (cause != null) {
				req.setAttribute("exception", cause);
				String causeMessage = cause.getMessage();
				if (!(e instanceof ClientSideException)) {
					message = cause.getMessage();
					if (message == null) {
						message = cause.getClass().getName();
					}
					String trace = ExceptionUtils.getStackTrace(cause);
					causeMessage = StringUtils.cut(trace.replace("\r\n", ""), 120);
					TRACE.debug(trace);
					req.setAttribute("error_trace", trace);
				}
				LOG.warn(causeMessage);
			} else {
				message = e.getClass().getName();
			}
			
			if (e instanceof HttpStatusException) {
				status = ((HttpStatusException) e).getStatusCode();
				LOG.error("ERROR " + status + " " + HttpStatusException.getReasonPhrase(e) + ", cause: " + message+ ", uri="+req.getRequestURI());
			}
			req.setAttribute("status_code", status);
			String page = getErrorPage(e);
			RequestDispatcher dispatcher = req.getRequestDispatcher(page);
			try {
				if (resp.isCommitted() == false) {
					resp.setStatus(status);
					LOG.debug("dispatch: " + page);
					dispatcher.forward(req, resp);
				} else {
					resp.getWriter().println(status + " " + HttpStatusException.getReasonPhrase(e));
					resp.getWriter().println(message);
				}
			} catch (Exception ex) {
				throw new InternalServerErrorException(null, ex);
			}
		}
	}
	
	protected String getErrorPage(Exception e) {
		int status = 500;
		if (e instanceof HttpStatusException) {
			status = ((HttpStatusException) e).getStatusCode();
		}
		String jsp = errorPagePath + "/" + status + extension;
		if (e instanceof AjaxActionException) {
			jsp = errorPagePath + "/" + ((AjaxActionException)e).getName() + extension;
		}
		return jsp;
	}
}