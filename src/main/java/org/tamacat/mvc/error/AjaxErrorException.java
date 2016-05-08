package org.tamacat.mvc.error;

public class AjaxErrorException extends AjaxActionException {

	private static final long serialVersionUID = 1L;

	public AjaxErrorException(String message) {
		super(500, message, message, null);
	}
	
	public AjaxErrorException(String message, Throwable cause) {
		super(500, message, message, cause);
	}

	public AjaxErrorException(int statusCode, String message, Throwable cause) {
		super(statusCode, message, cause);
	}
}
