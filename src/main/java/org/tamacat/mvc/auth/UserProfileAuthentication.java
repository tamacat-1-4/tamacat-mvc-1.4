package org.tamacat.mvc.auth;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tamacat.auth.model.DefaultUser;
import org.tamacat.auth.model.LoginUser;
import org.tamacat.auth.model.SingleSignOnSession;
import org.tamacat.auth.model.UserProfileUtils;
import org.tamacat.auth.util.EncryptSessionUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.error.ForbiddenException;
import org.tamacat.mvc.util.ServletUtils;
import org.tamacat.util.StringUtils;

public class UserProfileAuthentication implements Authentication {

	static final Log LOG = LogFactory.getLog(UserProfileAuthentication.class);
	
	protected String profileRequestAttributeKey = "login";
	protected String singleSignOnSessionCookieName = "SSOSession";
	protected String profileCookieName = "SSOProfile";

	protected String tableName = "users";
	protected String tidKey = "tid";
	protected String idKey = "id";
	protected String userKey = "user_id";
	protected String passwordKey = "password";
	protected String saltKey = "salt";
	protected String lastLoginKey = "last_login";
	protected String multiLoginKey = "multi_login";
	protected String loginStatusKey = "login_status";
	protected String roleKey;
	protected Set<String> columns = new LinkedHashSet<>();

	public void setProfileRequestAttributeKey(String profileRequestAttributeKey) {
		this.profileRequestAttributeKey = profileRequestAttributeKey;
	}
	
	public void setSingleSignOnSessionCookieName(String singleSignOnSessionCookieName) {
		this.singleSignOnSessionCookieName = singleSignOnSessionCookieName;
	}
	
	public void setUserProfileCookieName(String profileCookieName) {
		this.profileCookieName = profileCookieName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTidKey(String tidKey) {
		this.tidKey = tidKey;
	}
	
	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}
	
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	public void setSaltKey(String saltKey) {
		this.saltKey = saltKey;
	}
	
	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}
	
	public void setLastLoginKey(String lastLoginKey) {
		this.lastLoginKey = lastLoginKey;
	}
	
	public void setMultiLoginKey(String multiLoginKey) {
		this.multiLoginKey = multiLoginKey;
	}
	
	public void setLoginStatusKey(String loginStatusKey) {
		this.loginStatusKey = loginStatusKey;
	}
	
	public void setColumns(String value) {
		String[] cols = StringUtils.split(value, ",");
		for (String col : cols) {
			columns.add(col);
		}
	}
	
	public LoginUser createLoginUser() {
		return new DefaultUser(tableName, tidKey, idKey, userKey, passwordKey,
			saltKey,roleKey, lastLoginKey, multiLoginKey, loginStatusKey,
			columns.toArray(new String[columns.size()]));
	}
	
	@Override
	public boolean login(HttpServletRequest req, HttpServletResponse resp, String user, String password)
			throws ServletException {
		return false;
	}

	@Override
	public void logout(HttpServletRequest req, HttpServletResponse resp, String username) {
	}

	@Override
	public String check(HttpServletRequest req, HttpServletResponse resp) {
		LoginUser login = null;
		String session = ServletUtils.getCookie(req, singleSignOnSessionCookieName);
		if (StringUtils.isNotEmpty(session)) {
			SingleSignOnSession sso = SingleSignOnSession.parseSession(EncryptSessionUtils.decryptSession(session));
			if (sso != null) {
				String sessionUsername = sso.getUsername();
				String profile = ServletUtils.getCookie(req, profileCookieName);
				if (StringUtils.isNotEmpty(profile)) {
					LoginUser check = UserProfileUtils.getProfile(profile, createLoginUser(), columns);
					if (sessionUsername != null && check != null && sessionUsername.equals(check.get(userKey))) {
						setRequestAttribute(req, check);
						login = check;
					}
				}
			}
		}
		if (login == null || StringUtils.isEmpty(login.get(userKey))) {
			throw new ForbiddenException();
		}
		return (String) login.get(userKey);
	}
	
	public void setRequestAttribute(HttpServletRequest req, LoginUser loginUser) {
		LOG.trace("login="+loginUser);
		req.setAttribute(profileRequestAttributeKey, loginUser);
	}
	
	@Override
	public String getStartUrl(HttpServletRequest req) {
		return null;
	}

	@Override
	public String getLoginPage(HttpServletRequest req) {
		return null;
	}

	@Override
	public String getLogoutPage(HttpServletRequest req) {
		return null;
	}

	@Override
	public String generateOneTimePassword() {
		return null;
	}

}
