package org.tamacat.mvc.error;

public class AjaxWarningException extends AjaxActionException implements ClientSideException {

	private static final long serialVersionUID = 1L;

	public AjaxWarningException(String message) {
		super(412, message, message, null);
	}
	
	public AjaxWarningException(String message, Throwable cause) {
		super(412, message, message, cause);
	}

	public AjaxWarningException(int statusCode, String message, Throwable cause) {
		super(statusCode, message, cause);
	}
	public AjaxWarningException(int statusCode, String message) {
		super(statusCode, message, null);
	}
	
	@Override
	public String getName() {
		return "ajax_warning";
	}
}
