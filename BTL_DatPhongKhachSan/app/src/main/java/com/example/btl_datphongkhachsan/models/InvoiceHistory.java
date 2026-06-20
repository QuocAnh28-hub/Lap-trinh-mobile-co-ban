package com.example.btl_datphongkhachsan.models;

public class InvoiceHistory {
    private int StayID;
    private String FullName;
    private String LatestDate;
    private double TotalAmount;
    private String Status;

    public int getStayID() { return StayID; }
    public String getFullName() { return FullName; }
    public String getLatestDate() { return LatestDate; }
    public double getTotalAmount() { return TotalAmount; }
    public String getStatus() { return Status; }
}
