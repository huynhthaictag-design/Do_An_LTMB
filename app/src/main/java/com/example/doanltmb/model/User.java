package com.example.doanltmb.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String phone;

    public User(int userId, String username, String password, String role, String phone) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
