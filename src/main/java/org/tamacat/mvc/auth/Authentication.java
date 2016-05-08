/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.auth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authentication {
	String LOGIN = "Authentication.LOGIN";
	String USER = "Authentication.USER";

	boolean login(HttpServletRequest req, HttpServletResponse resp, String user, String password) throws ServletException;
	
	void logout(HttpServletRequest req, HttpServletResponse resp, String username);

	String check(HttpServletRequest req, HttpServletResponse resp);

	String getStartUrl(HttpServletRequest req);
	
	String getLoginPage(HttpServletRequest req);

	String getLogoutPage(HttpServletRequest req);
	
	String generateOneTimePassword();
}
