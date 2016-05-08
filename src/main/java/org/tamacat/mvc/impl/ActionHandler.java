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
import javax.servlet.http.HttpSession;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.RequestHandler;
import org.tamacat.mvc.action.ActionDefine;
import org.tamacat.mvc.action.ActionProcessor;
import org.tamacat.mvc.auth.Authentication;
import org.tamacat.mvc.auth.AuthorizedHttpServletRequest;
import org.tamacat.mvc.error.HttpStatusException;
import org.tamacat.mvc.error.InternalServerErrorException;
import org.tamacat.mvc.error.NotFoundException;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.StringUtils;

public class ActionHandler implements RequestHandler {

	static final Log LOG = LogFactory.getLog(ActionHandler.class);

	protected ActionProcessor processor;
	protected String packageName;
	protected Authentication authentication;
	protected String loginUsernameKey = "j_username";
	protected String loginPasswordKey = "j_password";
	protected String loginPath = "/login";

	protected String[] actionNotFoundPath = new String[] {
			".", "\\", "`", "<", ">", ":", "'", "\"", "|"
	}
	;
	static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	public ActionHandler() {
		processor = new ActionProcessor();
	}

	public ActionHandler(ActionProcessor processor) {
		this.processor = processor;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setActionProcessor(ActionProcessor processor) {
		this.processor = processor;
	}

	public void setLoginUsernameKey(String loginUsernameKey) {
		this.loginUsernameKey = loginUsernameKey;
	}

	public void setLoginPasswordKey(String loginPasswordKey) {
		this.loginPasswordKey = loginPasswordKey;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}
	
	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
		if (authentication != null) {
			try {
				// FORM LOGIN USERNAME AND PASSWORD CHECK.
				if (req.getRequestURI().endsWith(loginPath)) {
					String username = req.getParameter(loginUsernameKey);
					String password = req.getParameter(loginPasswordKey);
					if ("POST".equalsIgnoreCase(req.getMethod())) {
						if (authentication.login(req, resp, username, password)) {
							handleLoginRequest(req, resp, username);
							return;
						} else {
							req.setAttribute("login_error", true);
							handleLoginErrorRequest(req, resp);
							return;
						}
					} else {
						String checkedUsername = authentication.check(req, resp);
						if (StringUtils.isNotEmpty(checkedUsername)) {
							handleLoginRequest(req, resp, checkedUsername);
							return;
						}
					}
				}
				// ALREADY LOGIN SESSION CHECK.
				String username = authentication.check(req, resp);
				if (StringUtils.isNotEmpty(username)) {
					req = new AuthorizedHttpServletRequest(req, username);
					LOG.debug("auth check true. user=" + req.getRemoteUser());
					if (req.getRequestURI().endsWith("/logout")) {
						handleLogoutRequest(req, resp, username);
						return;
					}
				} else {
					handleLoginErrorRequest(req, resp);
					return;
				}
			} catch (HttpStatusException e) {
				throw e;
			} catch (Exception e) {
				throw new InternalServerErrorException(e.getMessage(), e);
			}
		}
		if (StringUtils.isNotEmpty(packageName)) {
			ActionDefine def = getActionDefine(req.getRequestURI());
			if (def != null) {
				processor.execute(def, req, resp);
			}
		}
	}
	
	protected void handleLoginRequest(HttpServletRequest req, HttpServletResponse resp, String username) throws IOException {
		req = new AuthorizedHttpServletRequest(req, username);
		LOG.debug("login true. user=" + req.getRemoteUser());
		setDefaultContentType(resp);
		resp.sendRedirect(req.getContextPath() + authentication.getStartUrl(req));
	}
	
	protected void handleLoginErrorRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		sessionInvalidate(req);
		req.setAttribute("key", authentication.generateOneTimePassword());
		LOG.debug("auth check false");
		new LoginDispatcher(authentication.getLoginPage(req)).dispatcher(req, resp);
	}
	
	protected void handleLogoutRequest(HttpServletRequest req, HttpServletResponse resp, String username) throws IOException, ServletException {
		authentication.logout(req, resp, username);
		sessionInvalidate(req);
		req.setAttribute("startUrl", authentication.getStartUrl(req));
		new LoginDispatcher(authentication.getLogoutPage(req)).dispatcher(req, resp);
	}
	
	protected void sessionInvalidate(HttpServletRequest req) {
		try {
			HttpSession session = req.getSession(false);
			if (session != null) {
				session.invalidate();
			}
		} catch (Exception e) {
		}
	}
	
	protected void setDefaultContentType(HttpServletResponse resp) {
		resp.setContentType(DEFAULT_CONTENT_TYPE);
	}

	protected ActionDefine getActionDefine(String uri) {
		if (uri != null) {
			for (String ch : actionNotFoundPath) {
				if (uri.indexOf(ch) >= 0) {
					throw new NotFoundException();
				}
			}
			try {
				Path path = Paths.get(uri);
				String className = getClassName(path);
				String action = getActionName(path);
				if (StringUtils.isNotEmpty(className) && StringUtils.isNotEmpty(action)) {
					return new ActionDefine(className, action);
				}
			} catch (Exception e) { //java.nio.file.InvalidPathException
				throw new NotFoundException("uri="+uri, e);
			}
		}
		return null;
	}

	protected String getClassName(Path path) {
		int count = path.getNameCount();
		LOG.trace("path=" + path + ", count=" + count);
		if (count <= 2) {
			return null;
		}
		String className = path.getName(count - 2).normalize().toString();
		if (className.indexOf('_') >= 0) {
			String[] sep = className.split("_");
			StringBuilder names = new StringBuilder();
			for (String val : sep) {
				names.append(ClassUtils.getCamelCaseName(val));
			}
			className = names.toString();
		} else {
			className = ClassUtils.getCamelCaseName(className);
		}
		return packageName + "." + className + "Action";
	}

	protected String getSplitUnderscoreClassName(Path path) {
		int count = path.getNameCount();
		LOG.trace("path=" + path + ", count=" + count);
		if (count <= 2) {
			return null;
		}
		String className = path.getName(count - 2).normalize().toString();
		if (className.indexOf('_') >= 0) {
			className = className.replaceFirst("_", ".");
			String[] pkgClass = className.split("\\.");
			if (pkgClass.length >= 2) {
				className = pkgClass[0] + "." + ClassUtils.getCamelCaseName(pkgClass[1]);
			}
		} else {
			className = ClassUtils.getCamelCaseName(className);
		}
		return packageName + "." + className + "Action";
	}

	protected String getActionName(Path path) {
		int count = path.getNameCount();
		if (count <= 2) {
			return null;
		}
		String action = path.getName(count - 1).normalize().toString();
		int idx = action.indexOf(".");
		if (idx >= 0) {
			action = action.substring(0, idx);
		}
		return action;
	}

	class LoginDispatcher extends DefaultDispatcher {
		String path;

		LoginDispatcher(String path) {
			this.path = path;
		}

		@Override
		public void dispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			setDefaultContentType(resp);
			String path = getLoginPath();
			LOG.debug("dispatch: " + path);
			RequestDispatcher dispatcher = req.getRequestDispatcher(path);
			dispatcher.forward(req, new DispatchHttpResponse(resp));
		}

		protected String getLoginPath() {
			return rootPath + path;// "/login/login.jsp";
		}
	}
}
