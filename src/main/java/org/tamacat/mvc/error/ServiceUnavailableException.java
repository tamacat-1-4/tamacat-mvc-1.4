/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class ServiceUnavailableException extends HttpStatusException {

	private static final long serialVersionUID = 9103097473855961423L;

	public ServiceUnavailableException() {
		super(500, "Service Unavailable", null, null);
	}

	public ServiceUnavailableException(String message, Throwable cause) {
		super(500, "Service Unavailable", message, cause);
	}
}
