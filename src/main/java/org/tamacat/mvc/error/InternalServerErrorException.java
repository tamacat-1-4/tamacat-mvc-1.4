/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class InternalServerErrorException extends HttpStatusException {

	private static final long serialVersionUID = 7156542508755575857L;

	public InternalServerErrorException() {
		super(500, "Internal Server Error", null, null);
	}

	public InternalServerErrorException(String message, Throwable cause) {
		super(500, "Internal Server Error", message, cause);
	}
}
