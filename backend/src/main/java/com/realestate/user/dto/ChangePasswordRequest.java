package com.realestate.user.dto;

public class ChangePasswordRequest {

    private String currentPassword; //verify current password
    private String newPassword;

    public ChangePasswordRequest() {
    }

    // getters and setters

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    // Do not accept fields outside this request's explicit contract.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}