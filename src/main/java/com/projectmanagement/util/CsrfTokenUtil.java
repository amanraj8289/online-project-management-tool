package com.projectmanagement.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF Protection Utility.
 * Generates and validates cryptographic session-based anti-CSRF tokens.
 */
public final class CsrfTokenUtil {

    public static final String CSRF_SESSION_ATTR = "csrfToken";
    public static final String CSRF_PARAM_NAME = "_csrf";
    public static final String CSRF_HEADER_NAME = "X-CSRF-Token";

    private static final SecureRandom secureRandom = new SecureRandom();

    private CsrfTokenUtil() {
    }

    /**
     * Generates or retrieves the existing CSRF token for the current session.
     */
    public static String getOrCreateToken(HttpSession session) {
        if (session == null) return "";
        String token = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (token == null) {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_SESSION_ATTR, token);
        }
        return token;
    }

    /**
     * Validates whether the submitted token matches the session token.
     */
    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (sessionToken == null) return false;

        String paramToken = request.getParameter(CSRF_PARAM_NAME);
        if (paramToken == null) {
            paramToken = request.getHeader(CSRF_HEADER_NAME);
        }

        return sessionToken.equals(paramToken);
    }
}
