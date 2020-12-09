package org.tamacat.mvc.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.RequestHandler;
import org.tamacat.mvc.action.ActionDefine;
import org.tamacat.mvc.action.ActionProcessor;
import org.tamacat.mvc.auth.Authentication;
import org.tamacat.mvc.error.UnauthorizedException;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.StringUtils;

public class AjaxActionHandler implements RequestHandler {
	
	static final Log LOG = LogFactory.getLog(AjaxActionHandler.class);
	static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	protected ActionProcessor processor;
	protected String packageName;
	protected Authentication authentication;
	
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setActionProcessor(ActionProcessor processor) {
		this.processor = processor;
	}

	
	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
		if (authentication != null) {
			String user = authentication.check(req, resp);
			if (StringUtils.isEmpty(user)) {
				throw new UnauthorizedException();
			}
		}
		if (processor != null && StringUtils.isNotEmpty(packageName)) {
			ActionDefine def = getActionDefine(req.getRequestURI());
			if (def != null) {
				processor.execute(def, req, resp);
			}
		}
	}

	
	protected void setDefaultContentType(HttpServletResponse resp) {
		resp.setContentType(DEFAULT_CONTENT_TYPE);
	}

	protected ActionDefine getActionDefine(String uri) {
		if (uri != null) {
			Path path = Paths.get(uri);
			String className = getClassName(path);
			String action = getActionName(path);
			if (StringUtils.isNotEmpty(className) && StringUtils.isNotEmpty(action)) {
				return new ActionDefine(className, action);
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
