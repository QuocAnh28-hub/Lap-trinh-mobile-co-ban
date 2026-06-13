package com.example.btl_datphongkhachsan.models;

public class RegisterRequest {
    private String FullName;
    private String Phone;
    private String Email;
    private String PasswordHash;

    public RegisterRequest(String fullName, String phone, String email, String passwordHash) {
        this.FullName = fullName;
        this.Phone = phone;
        this.Email = email;
        this.PasswordHash = passwordHash;
    }
}