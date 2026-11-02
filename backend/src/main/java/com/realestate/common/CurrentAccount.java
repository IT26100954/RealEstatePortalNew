package com.realestate.common;

public class CurrentAccount {
    private final String id;
    private final String role;
    public CurrentAccount(String id, String role) { this.id = id; this.role = role; }
    public String getId() { return id; }
    public String getRole() { return role; }
    public boolean isBuyer() { return "BUYER".equals(role); }
    public boolean isSeller() { return "SELLER".equals(role); }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
