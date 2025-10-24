package com.example.milkdelivery.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long customerId;
    private String phone;

    public AuthResponse(String accessToken, String refreshToken, Long customerId, String phone) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.customerId = customerId;
        this.phone = phone;
    }

    // getters + setters
}
