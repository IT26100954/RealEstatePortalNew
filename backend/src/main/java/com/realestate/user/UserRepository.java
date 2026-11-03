package com.realestate.user;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    long countByRole(Role role);

    // A small coursework account table: serialize account security changes.
    // This also prevents two simultaneous demotions deleting the last ADMIN.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u order by u.id")
    List<User> lockAllUsers();
}
