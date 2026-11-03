package com.realestate.user;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.realestate.common.CsrfTokens;
import com.realestate.common.CurrentUser;
import com.realestate.common.ModuleException;
import com.realestate.user.dto.AuthResponse;
import com.realestate.user.dto.LoginRequest;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserSessions sessions;
    private final String dummyHash;
    public AuthServiceImpl(UserRepository repository,PasswordEncoder encoder,UserSessions sessions) {
        this.repository=repository;
        this.encoder=encoder;
        this.sessions=sessions;
        this.dummyHash=encoder.encode(java.util.UUID.randomUUID().toString());
    }
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request,HttpServletRequest httpRequest) {
        String username=request==null ? null : request.getUsername();
        String password=request==null ? null : request.getPassword();
        if (username==null || username.trim().isEmpty() || username.trim().length()>40 || password==null || password.length()>128) throw failed();
        // Same lock order as password/role changes; verify current credentials.
        List<User> users=repository.lockAllUsers();
        User found=null;
        for (User user:users) { if (user.getUsername().equalsIgnoreCase(username.trim())) { found=user; break; } }
        if (found==null) { encoder.matches(password,dummyHash); throw failed(); }
        if (!encoder.matches(password,found.getPasswordHash())) throw failed();
        HttpSession previous=httpRequest.getSession(false);
        if (previous!=null) previous.invalidate();
        HttpSession session=httpRequest.getSession(true);
        session.setMaxInactiveInterval(30*60);
        session.setAttribute(CurrentUser.SESSION_USER_ID,found.getId());
        sessions.track(found.getId(),session);
        return new AuthResponse(UserViews.response(found),CsrfTokens.getOrCreate(session));
    }
    private ModuleException failed() { return new ModuleException(HttpStatus.UNAUTHORIZED,"LOGIN_FAILED","Invalid username or password."); }
}
