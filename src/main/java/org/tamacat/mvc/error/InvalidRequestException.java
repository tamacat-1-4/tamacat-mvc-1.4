/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class InvalidRequestException extends AjaxActionException implements ClientSideException {

	private static final long serialVersionUID = 1L;

	public InvalidRequestException() {
		super(400, "", null);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(400, message, cause);
	}

	public InvalidRequestException(String message) {
		super(400, message, null);
	}

	public InvalidRequestException(Throwable cause) {
		super(400, cause.getMessage(), cause);
	}

}
