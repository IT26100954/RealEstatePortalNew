package com.realestate.user.dto;

import com.realestate.user.Role;

public class ChangeRoleRequest {

    //Authorized admin
    private Role role;

    public ChangeRoleRequest() {
    }

    // Getters and setters

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    // Do not accept fields outside this request's explicit contract.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}