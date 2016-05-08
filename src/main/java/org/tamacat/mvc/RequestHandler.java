/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The Interface of request handring.
 */
public interface RequestHandler {

	void handleRequest(HttpServletRequest req, HttpServletResponse resp);
}
