package com.realestate.user;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserSessionListenerConfig {
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> userSessionListener(final UserSessions sessions) {
        HttpSessionListener listener=new HttpSessionListener() {
            @Override
            public void sessionDestroyed(HttpSessionEvent event) {
                sessions.remove(event.getSession());
            }
        };
        return new ServletListenerRegistrationBean<>(listener);
    }
}
