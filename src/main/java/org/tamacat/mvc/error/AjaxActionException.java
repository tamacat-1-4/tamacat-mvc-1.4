package org.tamacat.mvc.error;

public class AjaxActionException extends HttpStatusException {

	private static final long serialVersionUID = 1L;

	public AjaxActionException(int statusCode, String reasString,
			String message, Throwable cause) {
		super(statusCode, reasString, message, cause);
	}
	
	public AjaxActionException(int statusCode, String message, Throwable cause) {
		super(statusCode, message, message, cause);
	}

	public String getName() {
		return "ajax_error";
	}
}
