package org.tamacat.mvc.auth;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.tamacat.auth.model.LoginUser;

public class AbstractAuthenticationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testLogin() throws Exception {
		MockServletContext context = new MockServletContext("/test");
		MockHttpServletRequest req = new MockHttpServletRequest(context, "GET","/app/portal/main");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		req.addParameter("j_username", "guest");
		
		String username = "guest";
		String password = "password";
		auth.login(req, resp, username, password);
	}

	@Test
	public void testEncryptSession() {
		String user = "admin1234@example.com";
		String sessionid = "ba90cd06866c5f71846a7b39e78f9b24715c3ed1315d597cf5d6d388b1b21753";
		String time = "172580368621090";
		String encrypted = auth.encryptSession(user+"\t"+sessionid+"\t"+time);
		//System.out.println(encrypted);
		assertEquals("4S0-zlaeSv7_Smd4Z6YDGuqx-YIdvXxyLcLWunMTQj28WO0ptV_r1QWCBt4p4EJixbaXFQQsZWfWvjAj6dxPfMg28mhrHAWZAGIn7nqjBT-Iu1W71mUJO_-5XSKziJ-8lF1TM-0YqrbR2G1uOpuS3g", encrypted);
		assertEquals(user+"\t"+sessionid+"\t"+time, auth.decryptSession(encrypted));
	}
	
	AbstractAuthentication auth = new AbstractAuthentication() {

		@Override
		protected LoginUser getUser(String username) {
			return null;
		}
	};
}
