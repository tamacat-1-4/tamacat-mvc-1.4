package org.tamacat.mvc.auth;

import java.nio.file.attribute.UserPrincipal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.tamacat.auth.model.UserRole;

public class AuthorizedHttpServletRequest extends HttpServletRequestWrapper {

	protected String remoteUser;
	protected HttpUserPrincipal user;
	
	public AuthorizedHttpServletRequest(HttpServletRequest request, String remoteUser) {
		super(request);
		this.remoteUser = remoteUser;
		user = new HttpUserPrincipal();
	}
	
	@Override
	public String getRemoteUser() {
		return remoteUser;
	}
	
	@Override
	public java.security.Principal getUserPrincipal() {
		return user;
	}
	
    @Override
    public boolean isUserInRole(String role) {
    	Object user = getAttribute(Authentication.USER);
    	if (user != null && user instanceof UserRole) {
    		return ((UserRole)user).isUserInRole(role);
    	} else {
    		return false;
    	}
    }
	
	class HttpUserPrincipal implements UserPrincipal {
		@Override
		public String getName() {
			return remoteUser;
		}
	}
}
