package com.example.btl_datphongkhachsan.models;

public class RegisterResponse {
    private String message;
    private Data data;

    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        private String FullName;
        private String Phone;
        private String Email;

        public String getFullName() { return FullName; }
        public String getPhone() { return Phone; }
        public String getEmail() { return Email; }
    }
}