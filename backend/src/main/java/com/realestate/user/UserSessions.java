package com.realestate.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

// Tracks sessions in this single running backend, not across multiple servers.
@Component
public class UserSessions {
    private final Map<String,Set<HttpSession>> sessions=new HashMap<>();
    public synchronized void track(String userId,HttpSession session) {
        Set<HttpSession> values=sessions.get(userId);
        if (values==null) { values=new HashSet<>(); sessions.put(userId,values); }
        values.add(session);
    }
    public synchronized void remove(HttpSession session) {
        List<String> empty=new ArrayList<>();
        for (Map.Entry<String,Set<HttpSession>> entry:sessions.entrySet()) {
            entry.getValue().remove(session);
            if (entry.getValue().isEmpty()) empty.add(entry.getKey());
        }
        for (String key:empty) sessions.remove(key);
    }
    public void invalidate(String userId) {
        List<HttpSession> old=new ArrayList<>();
        synchronized(this) {
            Set<HttpSession> values=sessions.remove(userId);
            if (values!=null) old.addAll(values);
        }
        for (HttpSession session:old) {
            try { session.invalidate(); }
            catch (IllegalStateException e) { /* Already expired or logged out. */ }
        }
    }
    public void invalidateAfterCommit(final String userId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            invalidate(userId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { invalidate(userId); }
        });
    }
}
