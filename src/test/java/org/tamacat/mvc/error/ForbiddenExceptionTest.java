/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.error;

import static org.junit.Assert.*;

import org.junit.Test;

public class ForbiddenExceptionTest {

	@Test
	public void testForbiddenException() {
		assertEquals(403, new ForbiddenException().getStatusCode());
		assertEquals("Forbidden", new ForbiddenException().getReasonPhrase());
	}

	@Test
	public void testForbiddenExceptionMessage() {
		assertEquals(403, new ForbiddenException("TEST", null).getStatusCode());
		assertEquals("Forbidden", new ForbiddenException("TEST", null).getReasonPhrase());
	}
}
