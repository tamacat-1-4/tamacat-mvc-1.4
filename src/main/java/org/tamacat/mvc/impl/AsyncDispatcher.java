package org.tamacat.mvc.impl;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AsyncDispatcher extends DefaultDispatcher {
	
	@Override
	public void dispatcher(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = getPath(req.getRequestURI());
		LOG.debug("dispatch: " + path);
		
		AsyncContext ctx = req.getAsyncContext();
		if (ctx != null) ctx.dispatch(path);
	}
}
