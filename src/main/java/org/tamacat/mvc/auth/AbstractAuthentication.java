/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.mvc.auth;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.tamacat.auth.OneTimePassword;
import org.tamacat.auth.model.LoginUser;
import org.tamacat.auth.model.SingleSignOnSession;
import org.tamacat.auth.util.EncryptSessionUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.mvc.error.ServiceUnavailableException;
import org.tamacat.mvc.util.ServletUtils;
import org.tamacat.util.EncryptionUtils;
import org.tamacat.util.StringUtils;

public abstract class AbstractAuthentication implements Authentication {

	static final Log LOG = LogFactory.getLog(AbstractAuthentication.class);

	protected String startUrl = "/app/portal/main";
	protected String loginPage = "/login/login.jsp";
	protected String logoutPage = "/login/logout.jsp";
	protected boolean useSingleSignOn = true;
	protected String singleSignOnSessionKey = "SSOSession";
	protected String singleSignOnProfileKey = "SSOProfile";
	protected int stretch = 3;
	protected boolean encrypted = true;
	protected boolean isHttpOnlyCookie = true;
	protected boolean isSecureCookie;
	protected String singleSignOnCookiePath = "/";
	protected String loginKey = "key";
	protected String reverseProxyUserHeader = "X-ReverseProxy-User";
	protected OneTimePassword oneTimePassword;

	@Override
	public boolean login(HttpServletRequest req, HttpServletResponse resp, String username, String password) throws ServletException {
		if (!"POST".equalsIgnoreCase(req.getMethod())) {
			return false;
		}

		if (StringUtils.isEmpty(username) || username.length() > 255) {
			return false;
		}
		invalidateHttpSession(req);
		if (encrypted) {
			password = req.getParameter("encrypted");
			if (StringUtils.isEmpty(password)) {
				return false;
			}
		}
		String key = req.getParameter(loginKey);
		if (checkOneTimePassword(key, req) == false) {
			LOG.trace("OneTimePassword=false : " + key);
			return false;
		}
		LoginUser user = getUser(username);
		if (user == null || StringUtils.isEmpty(user.getUserId()) || StringUtils.isEmpty(user.getPassword())) {
			return false;
		}
		
		String authPassword = user.getPassword();
		if (user.isEncrypted() == false) {
			authPassword = getMessageDigest(authPassword); //Hashed password (SHA-256)
		}
		authPassword = getMessageDigest(authPassword + key);
		boolean check = user.getUserId().equals(username) && authPassword.equals(password);
		if (check) {
			if (useSingleSignOn == false) {
				req.getSession().setAttribute(LOGIN, username);
				req.getSession().setAttribute(USER, user);
			}
			req.setAttribute(USER, user);
			activate(req, resp, username, getUserSalt(username));
		}
		return check;
	}

	public void setOneTimePassword(OneTimePassword oneTimePassword) {
		this.oneTimePassword = oneTimePassword;
	}

	public boolean checkOneTimePassword(String key, HttpServletRequest req) {
		return oneTimePassword != null && oneTimePassword.check(getSecretKey(), key);
	}

	public String generateOneTimePassword() {
		return oneTimePassword != null ? oneTimePassword.generate(getSecretKey()) : null;
	}

	protected String getSecretKey() {
		return EncryptSessionUtils.getSecretKey();
	}
	
	protected abstract LoginUser getUser(String username);

	protected String getUserSalt(String username) {
		return generateSessionId(username, "", 0);
	}

	@Override
	public String check(HttpServletRequest req, HttpServletResponse resp) {
		if (useSingleSignOn) {
			String session = ServletUtils.getCookie(req, singleSignOnSessionKey);
			LOG.trace("check session=" + session);
			if (StringUtils.isNotEmpty(session) && checkSessionId(req, session)) {
				String decrypted = decryptSession(session);
				LOG.trace("decryptSession=" + decrypted);
				if (decrypted != null) {
					SingleSignOnSession sso = SingleSignOnSession.parseSession(decrypted);
					if (sso != null) {
						String username = sso.getUsername();
						resp.setHeader(reverseProxyUserHeader, username);
						return username;
					}
				}
			}
			// logout(req, resp);
			return null;
		} else {
			HttpSession session = req.getSession(false);
			if (session != null) {
				String username = (String) session.getAttribute(LOGIN);
				resp.setHeader(reverseProxyUserHeader, username);
				return username;
			}
			return null;
		}
	}

	protected void activate(HttpServletRequest req, HttpServletResponse resp, String username, String salt) {
		long time = System.nanoTime();		
		LoginUser user = (LoginUser) req.getAttribute(USER);
		if (! username.equalsIgnoreCase(user.getUserId())) {
			throw new ServiceUnavailableException();
		}
		String sessionId = generateSessionId(username, salt, time);
		String encrypted = encryptSession(username + "\t" + sessionId + "\t" + time);
		
		Cookie sessionCookie = new Cookie(singleSignOnSessionKey, encrypted);
		sessionCookie.setHttpOnly(isHttpOnlyCookie);
		sessionCookie.setSecure(isSecureCookie);
		sessionCookie.setPath(singleSignOnCookiePath);
		resp.addCookie(sessionCookie);
		
		String profile = user.toJson();
		if (profile != null) {
			Cookie profileCookie = new Cookie(singleSignOnProfileKey, encryptSession(profile));
			profileCookie.setHttpOnly(isHttpOnlyCookie);
			profileCookie.setSecure(isSecureCookie);
			profileCookie.setPath(singleSignOnCookiePath);
			resp.addCookie(profileCookie);
		}
	}

	protected String encryptSession(String session) {
		return EncryptSessionUtils.encryptSession(session);
	}
	
	protected String decryptSession(String session) {
		return EncryptSessionUtils.decryptSession(session);
	}

	@Override
	public void logout(HttpServletRequest req, HttpServletResponse resp, String username) {
		if (useSingleSignOn) {
			logoutSingleSignOn(resp);
		}
		invalidateHttpSession(req);
	}

	protected void invalidateHttpSession(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session != null) {
			try {
				session.invalidate();
			} catch (Exception e) {
				LOG.trace(e.getMessage());
			}
		}
	}
	
	protected void logoutSingleSignOn(HttpServletResponse resp) {
		Cookie sessionCookie = new Cookie(singleSignOnSessionKey, "");
		sessionCookie.setHttpOnly(isHttpOnlyCookie);
		sessionCookie.setSecure(isSecureCookie);
		sessionCookie.setPath(singleSignOnCookiePath);
		sessionCookie.setMaxAge(0);
		resp.addCookie(sessionCookie);
	}

	protected String generateSessionId(String username, String salt, long time) {
		String value = username + ":" + time + ":" + salt;
		for (int i = 0; i < stretch; i++) {
			String md = getMessageDigest(value);
			if (md != null) {
				value = md;
			}
		}
		return value;
	}

	protected String getMessageDigest(String value) {
		return EncryptionUtils.getMessageDigest(value, "SHA-256").toLowerCase();
	}

	protected boolean checkSessionId(HttpServletRequest req, String session) {
		String value = decryptSession(session);
		SingleSignOnSession sso = SingleSignOnSession.parseSession(value);
		if (sso != null) {
			String username = sso.getUsername();
			String salt = getUserSalt(username);
			LoginUser user = getUser(username);
			if (user != null) {
				String digest = generateSessionId(username, salt, StringUtils.parse(sso.getCreated(), 0L));
				if (LOG.isTraceEnabled()) {
					LOG.trace("user=" + username + ", salt=" + salt);
					LOG.trace("checkSessionId Session:digest=" + sso.getSessionId());
					LOG.trace("checkSessionId      DB:digest=" + digest);
				}
				boolean result = digest != null && digest.equals(sso.getSessionId());
				if (result) {
					req.setAttribute(USER, user);
				}
				return result;
			}
		}
		return false;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	public String getLogoutPage() {
		return logoutPage;
	}

	public void setLogoutPage(String logoutPage) {
		this.logoutPage = logoutPage;
	}

	@Override
	public String getStartUrl(HttpServletRequest req) {
		return startUrl;
	}

	@Override
	public String getLoginPage(HttpServletRequest req) {
		return loginPage;
	}

	@Override
	public String getLogoutPage(HttpServletRequest req) {
		return logoutPage;
	}

	public void setStretch(int stretch) {
		this.stretch = stretch;
	}

	public void setUseSingleSignOn(boolean useSingleSignOn) {
		this.useSingleSignOn = useSingleSignOn;
	}

	public void setSingleSignOnSessionKey(String singleSignOnSessionKey) {
		this.singleSignOnSessionKey = singleSignOnSessionKey;
	}
	
	public void setSingleSignOnProfileKey(String singleSignOnProfileKey) {
		this.singleSignOnProfileKey = singleSignOnProfileKey;
	}
	
	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public void setHttpOnlyCookie(boolean isHttpOnlyCookie) {
		this.isHttpOnlyCookie = isHttpOnlyCookie;
	}

	public void setSecureCookie(boolean isSecureCookie) {
		this.isSecureCookie = isSecureCookie;
	}

	public void setSingleSignOnCookiePath(String singleSignOnCookiePath) {
		this.singleSignOnCookiePath = singleSignOnCookiePath;
	}
	
	public void setReverseProxyUserHeader(String reverseProxyUserHeader) {
		this.reverseProxyUserHeader = reverseProxyUserHeader;
	}
}
