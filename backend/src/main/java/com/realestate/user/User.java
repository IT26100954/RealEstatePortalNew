package com.realestate.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


//map this class to the user table

@Entity //represent database records
@Table(name = "users") //connect the class to user table

public class User {
    //unique identifier
    @Id
    @Column(name = "id" , nullable = false , length = 36)
    private String id;

    //unique username
    @Column(name = "username" , nullable = false , unique = true , length = 40 )
    private String username;

    //password
    @Column(name = "password_hash" , nullable = false, length = 255)
    private String passwordHash;

    //contact information
    @Column(name = "email", nullable = false , length = 254)
    private String email;

    @Column(name = "phone", nullable = false , length = 20)
    private String phone;

    //store BUYER , SELLER , ADMIN as a text
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false , length = 20)
    private Role role;

    //default constructor
    public User(){

    }

    //getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
