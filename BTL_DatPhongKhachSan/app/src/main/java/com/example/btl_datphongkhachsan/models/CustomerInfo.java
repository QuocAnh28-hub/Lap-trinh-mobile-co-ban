package com.example.btl_datphongkhachsan.models;

import java.io.Serializable;

public class CustomerInfo implements Serializable {
    private String FullName;
    private String Email;
    private String Phone;
    private String CCCD;
    private String PasswordHash;
    private int TotalStay;

    public String getFullName() { return FullName; }
    public void setFullName(String fullName) { FullName = fullName; }
    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }
    public String getPhone() { return Phone; }
    public void setPhone(String phone) { Phone = phone; }
    public String getCCCD() { return CCCD; }
    public void setCCCD(String CCCD) { this.CCCD = CCCD; }
    public String getPasswordHash() { return PasswordHash; }
    public void setPasswordHash(String passwordHash) { PasswordHash = passwordHash; }
    public int getTotalStay() { return TotalStay; }
    public void setTotalStay(int totalStay) { TotalStay = totalStay; }
}