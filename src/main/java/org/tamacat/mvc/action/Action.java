/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

	String type() default "text/html; charset=UTF-8";
	
	//@Action(role={"admin","user"}) 
	String[] role() default "";
	
	//@Action(forward=/testapp) -> /WEB-INF/jsp/testapp/${action}/${method}.jsp
	String forward() default "";
}
