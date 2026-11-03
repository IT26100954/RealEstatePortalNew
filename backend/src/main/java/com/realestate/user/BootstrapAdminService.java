package com.realestate.user;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.realestate.common.ModuleException;
import static com.realestate.common.InputRules.*;

@Service
public class BootstrapAdminService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    public BootstrapAdminService(UserRepository repository,PasswordEncoder encoder) { this.repository=repository;this.encoder=encoder; }
    @Transactional
    public void createFirstAdmin(String username,String password,String email,String phone) {
        List<User> users=repository.lockAllUsers();
        for (User user:users) {
            if (user.getRole()==Role.ADMIN) throw new ModuleException(HttpStatus.CONFLICT,"ADMIN_EXISTS","An ADMIN already exists. Disable the bootstrap profile.");
        }
        if (password==null || password.length()<8 || password.length()>128) throw invalid("Bootstrap password must contain 8 to 128 characters.");
        String name=text(username,"Bootstrap username",40);
        conflict(repository.existsByUsernameIgnoreCase(name),"Bootstrap username is taken.");
        User admin=new User();
        admin.setId(UUID.randomUUID().toString());admin.setUsername(name);
        admin.setPasswordHash(encoder.encode(password));admin.setEmail(email(email));
        admin.setPhone(text(phone,"Phone",20));admin.setRole(Role.ADMIN);
        repository.saveAndFlush(admin);
    }
}
