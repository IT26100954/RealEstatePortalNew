package com.realestate.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CsrfTokens;
import com.realestate.common.CurrentAccount;
import com.realestate.common.CurrentUser;
import com.realestate.user.dto.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService users;
    private final AuthService auth;
    private final CurrentUser currentUser;
    public AuthController(UserService users,AuthService auth,CurrentUser currentUser) {
        this.users=users; this.auth=auth; this.currentUser=currentUser;
    }
    @GetMapping("/me")
    public AuthResponse me(HttpServletRequest request,HttpServletResponse response) {
        response.setHeader("Cache-Control","no-store");
        CurrentAccount actor=currentUser.optional(request.getSession(false));
        UserResponse user=null;
        if (actor!=null) user=users.getUserById(actor.getId(),actor.getId());
        return new AuthResponse(user,CsrfTokens.getOrCreate(request.getSession(true)));
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody CreateUserRequest request) { return users.register(request); }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request,HttpServletRequest httpRequest,HttpServletResponse response) {
        response.setHeader("Cache-Control","no-store");
        return auth.login(request,httpRequest);
    }
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if (session!=null) session.invalidate();
    }
}
