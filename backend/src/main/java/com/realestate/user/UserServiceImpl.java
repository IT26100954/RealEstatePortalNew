package com.realestate.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.realestate.common.ModuleException;
import com.realestate.user.dto.*;
import static com.realestate.common.InputRules.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JdbcTemplate jdbc;
    private final UserSessions sessions;

    // Constructor injection: Spring supplies these dependencies.
    public UserServiceImpl(UserRepository repository,PasswordEncoder encoder,JdbcTemplate jdbc,UserSessions sessions) {
        this.repository=repository;
        this.encoder=encoder;
        this.jdbc=jdbc;
        this.sessions=sessions;
    }
    private ModuleException missing() {
        return new ModuleException(HttpStatus.NOT_FOUND,"USER_NOT_FOUND","User not found.");
    }
    private User find(String userId) {
        java.util.Optional<User> found=repository.findById(id(userId));
        if (found.isEmpty()) throw missing();
        return found.get();
    }
    private User findIn(List<User> users,String userId) {
        String valid=id(userId);
        for (User user:users) { if (user.getId().equals(valid)) return user; }
        throw missing();
    }
    private User actor(String userId) {
        if (userId==null) throw new ModuleException(HttpStatus.UNAUTHORIZED,"LOGIN_REQUIRED","Please log in.");
        return find(userId);
    }
    private User actorIn(List<User> users,String userId) {
        if (userId==null) throw new ModuleException(HttpStatus.UNAUTHORIZED,"LOGIN_REQUIRED","Please log in.");
        return findIn(users,userId);
    }
    private void admin(User user) { allow(user.getRole()==Role.ADMIN); }
    private void unique(String name,String excludedId) {
        java.util.Optional<User> existing=repository.findByUsernameIgnoreCase(name);
        conflict(existing.isPresent() && !existing.get().getId().equals(excludedId),"Username is already taken.");
    }
    private String password(String value) {
        if (value==null || value.length()<8 || value.length()>128) throw invalid("Password must contain 8 to 128 characters.");
        return value;
    }
    private User newUser(CreateUserRequest request) {
        if (request==null) throw invalid("Request body is required.");
        if (request.getRole()==null) throw invalid("Role is required.");
        String username=text(request.getUsername(),"Username",40);
        unique(username,null);
        User user=new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(password(request.getPassword())));
        user.setEmail(email(request.getEmail()));
        user.setPhone(text(request.getPhone(),"Phone",20));
        user.setRole(request.getRole());
        return user;
    }
    private int references(String userId) {
        Integer count=jdbc.queryForObject(
            "SELECT (SELECT COUNT(*) FROM properties WHERE owner_id=?) + " +
            "(SELECT COUNT(*) FROM appointments WHERE buyer_id=?) + " +
            "(SELECT COUNT(*) FROM offers WHERE buyer_id=?) + " +
            "(SELECT COUNT(*) FROM reviews WHERE reviewer_id=?)",Integer.class,userId,userId,userId,userId);
        return count==null ? 0 : count;
    }
    private long admins(List<User> users) {
        long count=0;
        for (User user:users) { if (user.getRole()==Role.ADMIN) count++; }
        return count;
    }
    @Override
    public UserResponse register(CreateUserRequest request) {
        if (request==null) throw invalid("Request body is required.");
        if (request.getRole()==null) throw invalid("Role is required.");
        allow(request.getRole()==Role.BUYER || request.getRole()==Role.SELLER);
        return UserViews.response(repository.saveAndFlush(newUser(request)));
    }
    @Override
    public UserResponse createUser(String currentUserId,CreateUserRequest request) {
        List<User> users=repository.lockAllUsers();
        admin(actorIn(users,currentUserId));
        return UserViews.response(repository.saveAndFlush(newUser(request)));
    }
    @Override
    @Transactional(readOnly=true)
    public List<UserResponse> getAllUsers(String currentUserId) {
        admin(actor(currentUserId));
        List<UserResponse> responses=new ArrayList<>();
        for (User user:repository.findAll(Sort.by("id"))) responses.add(UserViews.response(user));
        return responses;
    }
    @Override
    @Transactional(readOnly=true)
    public UserResponse getUserById(String currentUserId,String userId) {
        User current=actor(currentUserId);
        User target=find(userId);
        allow(current.getRole()==Role.ADMIN || current.getId().equals(target.getId()));
        return UserViews.response(target);
    }
    @Override
    public UserResponse updateUser(String currentUserId,String userId,UpdateUserRequest request) {
        List<User> users=repository.lockAllUsers();
        User current=actorIn(users,currentUserId);
        User target=findIn(users,userId);
        allow(current.getRole()==Role.ADMIN || current.getId().equals(target.getId()));
        if (request==null) throw invalid("Request body is required.");
        String username=text(request.getUsername(),"Username",40);
        unique(username,target.getId());
        target.setUsername(username);
        target.setEmail(email(request.getEmail()));
        target.setPhone(text(request.getPhone(),"Phone",20));
        return UserViews.response(repository.saveAndFlush(target));
    }
    @Override
    public void changePassword(String currentUserId,String userId,ChangePasswordRequest request) {
        List<User> users=repository.lockAllUsers();
        User current=actorIn(users,currentUserId);
        User target=findIn(users,userId);
        allow(current.getId().equals(target.getId()));
        if (request==null || request.getCurrentPassword()==null || request.getCurrentPassword().length()>128 ||
            !encoder.matches(request.getCurrentPassword(),target.getPasswordHash())) throw invalid("Current password is incorrect.");
        target.setPasswordHash(encoder.encode(password(request.getNewPassword())));
        repository.saveAndFlush(target);
        sessions.invalidateAfterCommit(target.getId());
    }
    @Override
    public UserResponse changeRole(String currentUserId,String userId,ChangeRoleRequest request) {
        List<User> users=repository.lockAllUsers();
        admin(actorIn(users,currentUserId));
        User target=findIn(users,userId);
        if (request==null || request.getRole()==null) throw invalid("Role is required.");
        if (target.getRole()==request.getRole()) return UserViews.response(target);
        conflict(target.getRole()==Role.ADMIN && admins(users)<=1,"The last ADMIN cannot be demoted.");
        conflict(references(target.getId())>0,"Referenced accounts cannot change role.");
        target.setRole(request.getRole());
        return UserViews.response(repository.saveAndFlush(target));
    }
    @Override
    public void deleteUser(String currentUserId,String userId) {
        List<User> users=repository.lockAllUsers();
        User current=actorIn(users,currentUserId);
        admin(current);
        User target=findIn(users,userId);
        allow(!current.getId().equals(target.getId()));
        conflict(target.getRole()==Role.ADMIN && admins(users)<=1,"The last ADMIN cannot be deleted.");
        conflict(references(target.getId())>0,"Referenced accounts cannot be deleted.");
        repository.delete(target);
        repository.flush();
        sessions.invalidateAfterCommit(target.getId());
    }
}
