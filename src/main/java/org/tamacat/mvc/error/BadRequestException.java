/*
 * Copyright 2020 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class BadRequestException extends HttpStatusException implements ClientSideException {

	private static final long serialVersionUID = 6279699591610121662L;

	public BadRequestException() {
		super(400, "Bad Request", null, null);
	}

	public BadRequestException(String message) {
		super(400, "Bad Request\"", message, null);
	}
	
	public BadRequestException(String message, Throwable cause) {
		super(400, "Bad Request\"", message, cause);
	}
}
