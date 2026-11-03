package com.realestate.common;

import java.security.SecureRandom;
import java.util.Base64;
import jakarta.servlet.http.HttpSession;

public final class CsrfTokens {
    public static final String ATTRIBUTE="csrfToken";
    private CsrfTokens() { }
    public static String getOrCreate(HttpSession session) {
        Object existing=session.getAttribute(ATTRIBUTE);
        if (existing instanceof String) return (String) existing;
        byte[] bytes=new byte[32];
        new SecureRandom().nextBytes(bytes);
        String token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(ATTRIBUTE,token);
        return token;
    }
}
