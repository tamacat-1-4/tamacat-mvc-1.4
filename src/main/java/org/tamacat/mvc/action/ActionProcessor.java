/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.auth.model.UserRole;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.IOUtils;
import org.tamacat.util.StringUtils;
import org.tamacat.mvc.auth.Authentication;
import org.tamacat.mvc.error.ForbiddenException;
import org.tamacat.mvc.error.HttpStatusException;
import org.tamacat.mvc.error.InternalServerErrorException;
import org.tamacat.mvc.error.InvalidRequestException;
import org.tamacat.mvc.util.ServletUtils;

public class ActionProcessor {

	static final Log LOG = LogFactory.getLog(ActionProcessor.class);
	static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
	static final String ACTION_KEY = "org.tamacat.mvc.Action";

	public void execute(ActionDefine actionDef, HttpServletRequest req, HttpServletResponse resp) {
		ServletUtils.setActionDefine(req, actionDef);
		Class<?> type = ClassUtils.forName(actionDef.getName());
		Action action = null;
		Method m = null;
		if (type != null) {
			m = ClassUtils.getMethod(type, actionDef.getAction(),
					HttpServletRequest.class, HttpServletResponse.class);
			if (m != null) {
				action = m.getAnnotation(Action.class);
			}
			if (action == null) {
				action = type.getAnnotation(Action.class);
			}
			req.setAttribute(ACTION_KEY, action);
		}
		//Access Control
		checkUserInRoles(action, req, resp);

		//Set Content-Type response header
		setContentType(action, req, resp);
		if (m != null) {
			try {
				LOG.debug("actionName=" + actionDef.getActionName());
				Object o = ClassUtils.newInstance(type);
				try {
					m.invoke(o, req, resp);
				} catch (Exception e) {
					throw e;
				} finally {
					if (o instanceof AutoCloseable) {
						IOUtils.close(o);
					}
				}
			} catch (IllegalAccessException e) {
				throw new ForbiddenException(e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				// ignore
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof InvalidRequestException) {
					throw (InvalidRequestException)e.getCause();
				}
				if (e.getCause() instanceof HttpStatusException) {
					throw (HttpStatusException) e.getCause();
				}
				throw new InternalServerErrorException(
					e.getMessage(), e.getCause());
			}
		}
	}

	protected void checkUserInRoles(Action action, HttpServletRequest req, HttpServletResponse resp) {
		if (action != null) {
			String[] roles = action.role();
			Object user = req.getAttribute(Authentication.USER);
			if (user != null && user instanceof UserRole && roles != null
					&& roles.length > 0 && StringUtils.isNotEmpty(roles[0])) {
				boolean result = false;
				for (String role : roles) {
					result = ((UserRole)user).isUserInRole(role);
					LOG.debug("isUserInRole("+role+") = "+ result);
					if (result) {
						break;
					}
				}
				if (!result) {
					throw new ForbiddenException("Access denied. ["+req.getRequestURI()+"]");
				}
			}
		}
	}

	protected void setContentType(Action action, HttpServletRequest req, HttpServletResponse resp) {
		if (action != null) {
			String contentType = action.type();
			if (StringUtils.isNotEmpty(contentType)) {
				LOG.trace("contentType=" + contentType);
				resp.setContentType(contentType);
			}
		} else {
			resp.setContentType(DEFAULT_CONTENT_TYPE);
		}
	}
}
