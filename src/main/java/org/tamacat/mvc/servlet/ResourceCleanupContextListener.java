/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.servlet;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
//import javax.servlet.annotation.WebListener;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.sql.ConnectionManager;
import org.tamacat.sql.DBAccessManager;
import org.tamacat.sql.ResourceManager;

//@WebListener
public class ResourceCleanupContextListener implements ServletContextListener {

	static final Log LOG = LogFactory.getLog(ResourceCleanupContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			// All Objects cleanup
			LOG.info("Starting manager cleanup..");
			DBAccessManager.shutdown();
			ResourceManager.release();
			ConnectionManager.closeAll();

			LOG.info("Starting thread locals cleanup..");
			cleanThreadLocals();
		} catch (Throwable t) {
			LOG.warn(t);
		} finally {
			LOG.info("End resource cleanup");
		}
	}

	//https://weblogs.java.net/blog/jjviana/archive/2010/06/10/threadlocal-thread-pool-bad-idea-or-dealing-apparent-glassfish-memor
	protected void cleanThreadLocals() throws NoSuchFieldException,
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException {
		// ThreadLocal cleanup
		Thread[] threadgroup = new Thread[256];
		Thread.enumerate(threadgroup);
		for (int i = 0; i < threadgroup.length; i++) {
			if (threadgroup[i] != null) {
				cleanThreadLocals(threadgroup[i]);
			}
		}
	}
	
	//https://weblogs.java.net/blog/jjviana/archive/2010/06/10/threadlocal-thread-pool-bad-idea-or-dealing-apparent-glassfish-memor
	protected void cleanThreadLocals(Thread thread)
			throws NoSuchFieldException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException {
		Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
		threadLocalsField.setAccessible(true);

		Class<?> threadLocalMapKlazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
		Field tableField = threadLocalMapKlazz.getDeclaredField("table");
		tableField.setAccessible(true);

		Object fieldLocal = threadLocalsField.get(thread);
		if (fieldLocal == null) {
			return;
		}
		Object table = tableField.get(fieldLocal);
		int threadLocalCount = Array.getLength(table);
		for (int i = 0; i < threadLocalCount; i++) {
			Object entry = Array.get(table, i);
			if (entry != null) {
				Field valueField = entry.getClass().getDeclaredField("value");
				valueField.setAccessible(true);
				Object value = valueField.get(entry);
				if (value != null) {
					if (value.getClass().getName()
							.equals("com.sun.enterprise.security.authorize.HandlerData")) {
						valueField.set(entry, null);
					}
				}
			}
		}
	}
}
