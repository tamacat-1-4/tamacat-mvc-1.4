/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.servlet.debug;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

@WebListener
public class DebugHttpSessionListener implements HttpSessionListener {

	static final Log LOG = LogFactory.getLog(DebugHttpSessionListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		LOG.debug("created: " + event.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		LOG.debug("destroyed: " + event.getSession().getId());
	}
}
