/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

public class HttpStatusException extends RuntimeException {

	private static final long serialVersionUID = 2256017503327240724L;

	protected final int statusCode;
	protected final String reasonPhrase;

	public HttpStatusException(int statusCode, String reasString, String message, Throwable cause) {
		super(message == null && cause != null ? cause.getMessage() : message, cause);
		this.statusCode = statusCode;
		this.reasonPhrase = reasString;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
	public static String getReasonPhrase(Exception e) {
		if (e instanceof HttpStatusException) {
			return ((HttpStatusException) e).getReasonPhrase();
		} else {
			return e.getMessage();
		}
	}
}
