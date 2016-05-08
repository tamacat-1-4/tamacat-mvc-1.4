/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class NotFoundException extends HttpStatusException implements ClientSideException {

	private static final long serialVersionUID = 7156542508755575857L;

	public NotFoundException() {
		super(404, "Not Found", null, null);
	}

	public NotFoundException(String message, Throwable cause) {
		super(404, "Not Found", null, null);
	}
}
