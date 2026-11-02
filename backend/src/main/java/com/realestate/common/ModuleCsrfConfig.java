package com.realestate.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ModuleCsrfConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String method=request.getMethod();
                if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) return true;
                HttpSession session=request.getSession(false);
                Object expected=session == null ? null : session.getAttribute(CsrfTokens.ATTRIBUTE);
                String received=request.getHeader("X-CSRF-TOKEN");
                if (!(expected instanceof String) || received == null || !MessageDigest.isEqual(
                    ((String) expected).getBytes(StandardCharsets.UTF_8),received.getBytes(StandardCharsets.UTF_8))) {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\":\"CSRF_INVALID\",\"message\":\"Refresh your session and retry.\"}");
                    return false;
                }
                return true;
            }
        }).addPathPatterns("/api/**");
    }
}
