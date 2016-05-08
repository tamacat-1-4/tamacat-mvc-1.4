/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class ForbiddenException extends HttpStatusException implements ClientSideException {

	private static final long serialVersionUID = 6279699591610121662L;

	public ForbiddenException() {
		super(403, "Forbidden", null, null);
	}

	public ForbiddenException(String message) {
		super(403, "Forbidden", message, null);
	}
	
	public ForbiddenException(String message, Throwable cause) {
		super(403, "Forbidden", message, cause);
	}
}
