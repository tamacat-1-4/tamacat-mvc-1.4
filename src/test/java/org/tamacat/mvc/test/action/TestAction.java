package org.tamacat.mvc.test.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.mvc.action.Action;
import org.tamacat.mvc.util.ServletUtils;

public class TestAction {

	public void main(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("key1", "value1");
		ServletUtils.println(resp, "main");
	}
	
	@Action(type="application/json; charset=UTF-8", role="admin")
	public void admin(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("key1", "value1");
		ServletUtils.println(resp, "main");
	}
	
	@Action(type="application/json; charset=UTF-8")
	public void json(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("key1", "value1");
		ServletUtils.println(resp, "main");
	}
}
