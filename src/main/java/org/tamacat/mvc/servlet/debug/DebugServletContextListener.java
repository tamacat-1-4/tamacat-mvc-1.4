/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.servlet.debug;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

@WebListener()
public class DebugServletContextListener implements ServletContextListener {

	static final Log LOG = LogFactory.getLog(DebugServletContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		LOG.debug("contextInitialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LOG.debug("contextDestroyed");
	}

}
