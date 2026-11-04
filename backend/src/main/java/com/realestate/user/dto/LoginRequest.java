package com.realestate.user.dto;

public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    // Do not accept fields outside this request's explicit contract.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}