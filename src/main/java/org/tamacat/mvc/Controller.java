/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc;

public interface Controller extends RequestHandler, ExceptionHandler, Dispatcher {
	
	void setHandler(Object handler);
}
