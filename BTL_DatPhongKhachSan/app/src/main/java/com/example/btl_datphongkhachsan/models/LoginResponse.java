package com.example.btl_datphongkhachsan.models;

public class LoginResponse {
    private String message;
    private Account account;

    public String getMessage() { return message; }
    public Account getAccount() { return account; }

    public static class Account {
        private String UserID;
        private String Email;
        private String Role;
        private String FullName;
        private String Phone;

        public String getUserID() { return UserID; }
        public String getEmail() { return Email; }
        public String getRole() { return Role; }
        public String getFullName() { return FullName; }
        public String getPhone() { return Phone; }
    }
}