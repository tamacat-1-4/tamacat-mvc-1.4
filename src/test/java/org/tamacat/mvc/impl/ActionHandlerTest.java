/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.impl;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;
import org.tamacat.mvc.action.ActionDefine;

public class ActionHandlerTest {

	@Test
	public void testGetActionDefine() {
		ActionHandler handler = new ActionHandler();
		handler.setPackageName("org.tamacat.mvc.test");

		ActionDefine def = handler.getActionDefine("/testapp/app/test/main");
		assertEquals("org.tamacat.mvc.test.TestAction", def.getName());
		assertEquals("main", def.getAction());
	}

	@Test
	public void testGetActionDefineNull() {
		ActionHandler handler = new ActionHandler();
		handler.setPackageName("org.tamacat.mvc.test");

		assertNotNull(handler.getActionDefine("/testapp/app/test/main"));

		assertNull(handler.getActionDefine("/"));
		assertNull(handler.getActionDefine("/testapp"));
		assertNull(handler.getActionDefine("/testapp/"));
		assertNull(handler.getActionDefine("/testapp/app"));
		assertNull(handler.getActionDefine("/testapp/app/"));
		//assertNull(handler.getActionDefine("/testapp/app/test"));
		//assertNull(handler.getActionDefine("/testapp/app/test/"));

		assertNull(handler.getActionDefine(null));
		assertNull(handler.getActionDefine(""));
		assertNull(handler.getActionDefine("testapp"));
		//assertNull(handler.getActionDefine("testapp/app/test/"));
	}
	
	@Test
	public void testGetClassName() {
		ActionHandler handler = new ActionHandler();
		handler.setPackageName("org.tamacat.mvc.test");

		assertNotNull(handler.getClassName(Paths.get("/testapp/app/test/main")));
		assertEquals("org.tamacat.mvc.test.TestAction", handler.getClassName(Paths.get("/testapp/app/test/main")));
		assertEquals("org.tamacat.mvc.test.PageSettingsAction", handler.getClassName(Paths.get("/testapp/app/page_settings/main")));
	}
	
	@Test
	public void testGetSplitUnderscoreClassName() {
		ActionHandler handler = new ActionHandler();
		handler.setPackageName("org.tamacat.mvc.test");

		assertNotNull(handler.getSplitUnderscoreClassName(Paths.get("/testapp/app/test/main")));
		assertEquals("org.tamacat.mvc.test.TestAction", handler.getSplitUnderscoreClassName(Paths.get("/testapp/app/test/main")));
		assertEquals("org.tamacat.mvc.test.page.SettingsAction", handler.getSplitUnderscoreClassName(Paths.get("/testapp/app/page_settings/main")));
	}
}
