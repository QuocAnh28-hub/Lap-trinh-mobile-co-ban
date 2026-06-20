package com.example.btl_datphongkhachsan.models;

public class PenaltyUsage {
    private int PenaltyID;
    private String Reason;
    private double Amount;
    private String CreatedAt;

    public int getPenaltyID() { return PenaltyID; }
    public String getReason() { return Reason; }
    public double getAmount() { return Amount; }
    public String getCreatedAt() { return CreatedAt; }
}
