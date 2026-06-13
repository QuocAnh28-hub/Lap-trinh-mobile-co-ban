package com.example.btl_datphongkhachsan.models;

public class LoginRequest {
    private String Email;
    private String PasswordHash;

    public LoginRequest(String email, String passwordHash) {
        this.Email = email;
        this.PasswordHash = passwordHash;
    }
}