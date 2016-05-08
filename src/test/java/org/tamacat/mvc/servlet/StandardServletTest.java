/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.servlet;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.tamacat.mvc.Controller;
import org.tamacat.mvc.error.NotFoundException;
import org.tamacat.mvc.impl.DefaultController;
import org.tamacat.mvc.servlet.mock.MockServletConfig;

public class StandardServletTest {

	@Test
	public void testGetController() throws Exception {
		StandardServlet servlet = new StandardServlet();
		MockServletConfig config = new MockServletConfig("TestServlet");
		config.setInitParam("controller", "controller.xml");
		servlet.init(config);

		Controller controller = servlet.getController("app");
		assertEquals(DefaultController.class, controller.getClass());

		// controller = servlet.getController("/app/index.html");
		// assertEquals(DefaultController.class, controller.getClass());
		//
		// controller = servlet.getController("/app/test/index.html");
		// assertEquals(DefaultController.class, controller.getClass());

		controller = servlet.getController("default");
		assertEquals(DefaultController.class, controller.getClass());
	}
	
	@Test
	public void testGetRequest() throws Exception {
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "GET","/app/test/main");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		StandardServlet servlet = new StandardServlet();
		MockServletConfig config = new MockServletConfig("app");
		config.setInitParam("controller", "controller.xml");
		servlet.init(config);
		servlet.service(req, resp);
		assertEquals("value1", req.getAttribute("key1"));
		assertEquals("text/html; charset=UTF-8", resp.getContentType());
	}
	
	@Test
	public void testPostRequest() throws Exception {
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "POST","/app/test/main");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		StandardServlet servlet = new StandardServlet();
		MockServletConfig config = new MockServletConfig("app");
		config.setInitParam("controller", "controller.xml");
		servlet.init(config);
		servlet.service(req, resp);
		assertEquals("value1", req.getAttribute("key1"));
	}
	
	@Test
	public void testJsonContentType() throws Exception {
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "GET","/app/test/json");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		StandardServlet servlet = new StandardServlet();
		MockServletConfig config = new MockServletConfig("app");
		config.setInitParam("controller", "controller.xml");
		servlet.init(config);
		servlet.service(req, resp);
		assertEquals("value1", req.getAttribute("key1"));
		assertEquals("application/json; charset=UTF-8", resp.getContentType());
	}
	
	@Test
	public void testActionRoleAdmin() throws Exception {
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "GET","/app/test/admin");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		StandardServlet servlet = new StandardServlet();
		MockServletConfig config = new MockServletConfig("app");
		config.setInitParam("controller", "controller.xml");
		servlet.init(config);
		servlet.service(req, resp);
		assertEquals("value1", req.getAttribute("key1"));
		assertEquals("application/json; charset=UTF-8", resp.getContentType());
	}
	
	@Test
	public void testErrorRequest() throws Exception {
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "GET","/test/");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		StandardServlet servlet = new StandardServlet();
		MockServletConfig config = new MockServletConfig("test");
		config.setInitParam("controller", "controller.xml");
		servlet.init(config);
		try {
			servlet.service(req, resp);
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof NotFoundException);
		}
	}
}
