package com.example.milkdelivery.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    @NotBlank private String phone;
    @NotBlank private String password;
    // getters & setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
