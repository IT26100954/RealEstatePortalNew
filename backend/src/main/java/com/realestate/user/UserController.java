package com.realestate.user;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CurrentUser;
import com.realestate.user.dto.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;                           // Association: the controller stores and uses the service.
    private final CurrentUser currentUser;
    public UserController(UserService service,CurrentUser currentUser) { this.service=service; this.currentUser=currentUser; }
    @GetMapping
    public List<UserResponse> list(HttpSession session) { return service.getAllUsers(currentUser.required(session).getId()); }
    @GetMapping("/{id}")
    public UserResponse detail(@PathVariable String id,HttpSession session) { return service.getUserById(currentUser.required(session).getId(),id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody CreateUserRequest request,HttpSession session) { return service.createUser(currentUser.required(session).getId(),request); }
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable String id,@RequestBody UpdateUserRequest request,HttpSession session) { return service.updateUser(currentUser.required(session).getId(),id,request); }
    @PutMapping("/{id}/role")
    public UserResponse role(@PathVariable String id,@RequestBody ChangeRoleRequest request,HttpSession session) { return service.changeRole(currentUser.required(session).getId(),id,request); }
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void password(@PathVariable String id,@RequestBody ChangePasswordRequest request,HttpSession session) { service.changePassword(currentUser.required(session).getId(),id,request); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,HttpSession session) { service.deleteUser(currentUser.required(session).getId(),id); }
}
