/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultDispatcherTest {

	@Test
	public void testGetPath() {
		DefaultDispatcher dispatcher = new DefaultDispatcher();
		dispatcher.extension = ".jsp";
		dispatcher.setRootPath("/WEB-INF/jsp");

		assertEquals("/WEB-INF/jsp/upload.jsp", dispatcher.getPath("/upload"));
	}

	@Test
	public void testGetPathWithExtension() {
		DefaultDispatcher dispatcher = new DefaultDispatcher();
		dispatcher.extension = ".jsp";
		dispatcher.setRootPath("/WEB-INF/jsp");

		assertEquals("/WEB-INF/jsp/upload.jsp", dispatcher.getPath("/upload.html"));

		assertEquals("/WEB-INF/jsp/upload.htm", dispatcher.getPath("/upload.htm"));
		assertEquals("/WEB-INF/jsp/upload.png", dispatcher.getPath("/upload.png"));
	}
}
