package org.tamacat.mvc.auth;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

public class DBAuthenticationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLogin() throws Exception {
		DBAuthentication auth = new DBAuthentication();
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "POST","/app/test/admin");
		req.addParameter("key", "1234567890");
		req.addParameter("encrypted", "password");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		String username = "admin";
		String password = "";
		
		auth.login(req, resp, username, password);
	}

	@Test
	public void testGetUser() {
		DBAuthentication auth = new DBAuthentication();
		auth.getUser("admin");
	}

	@Test
	public void testSetColumnsString2() {
		DBAuthentication auth1 = new DBAuthentication();
		auth1.setColumns("users.username, ,users.email");
		assertEquals(2, auth1.columnList.size());
		
		DBAuthentication auth = new DBAuthentication();
		auth.setColumns("users.username, users.email");
		assertEquals(2, auth.columnList.size());
		
		auth.getUser("admin");
	}
	
	@Test
	public void testSetColumnsString() {
		DBAuthentication auth = new DBAuthentication();
		auth.setColumns(null);
		assertEquals(0, auth.columnList.size());
		auth.setColumns("");
		assertEquals(0, auth.columnList.size());
		
		auth.setColumns("users.username");
		assertEquals(1, auth.columnList.size());
		
		auth.getUser("admin");
	}
	
	@Test
	public void testGetUserSalt() {
	}

	@Test
	public void testCheck() {
	}

	@Test
	public void testLogout() {
	}

	@Test
	public void testUpdateUserSalt() {
	}

}
