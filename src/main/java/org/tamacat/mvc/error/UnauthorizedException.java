/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class UnauthorizedException extends HttpStatusException implements ClientSideException {

	private static final long serialVersionUID = -9190240085005455694L;

	public UnauthorizedException() {
		super(401, "Unauthorized", null, null);
	}

	public UnauthorizedException(String message) {
		super(401, "Unauthorized", message, null);
	}
	
	public UnauthorizedException(String message, Throwable cause) {
		super(401, "Unauthorized", message, cause);
	}
}
