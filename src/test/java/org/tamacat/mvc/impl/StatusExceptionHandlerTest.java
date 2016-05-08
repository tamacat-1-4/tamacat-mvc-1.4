package org.tamacat.mvc.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.tamacat.mvc.error.AjaxErrorException;
import org.tamacat.mvc.error.AjaxWarningException;
import org.tamacat.mvc.error.ForbiddenException;
import org.tamacat.mvc.error.NotFoundException;

public class StatusExceptionHandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetErrorPage() {
		StatusExceptionHandler handler = new StatusExceptionHandler();
		
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "GET","/app/test/main");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		
		assertEquals("/WEB-INF/jsp/error/ajax_warning.jsp", handler.getErrorPage(new AjaxWarningException("WARNING")));
		
		handler.handleException(req, resp, new AjaxWarningException("WARNING"));
	}

	@Test
	public void testGetErrorPage500() {
		StatusExceptionHandler handler = new StatusExceptionHandler();
		
		assertEquals("/WEB-INF/jsp/error/ajax_error.jsp", handler.getErrorPage(new AjaxErrorException("ERROR")));
		assertEquals("/WEB-INF/jsp/error/ajax_warning.jsp", handler.getErrorPage(new AjaxWarningException("WARNING")));

		assertEquals("/WEB-INF/jsp/error/500.jsp", handler.getErrorPage(new Exception("ERROR")));
		assertEquals("/WEB-INF/jsp/error/403.jsp", handler.getErrorPage(new ForbiddenException()));
		assertEquals("/WEB-INF/jsp/error/404.jsp", handler.getErrorPage(new NotFoundException()));
		assertEquals("/WEB-INF/jsp/error/500.jsp", handler.getErrorPage(new NullPointerException()));
	}
}
