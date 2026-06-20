package com.example.btl_datphongkhachsan.models;

public class ServiceUsage {
    private int UsageID;
    private String ServiceName;
    private int Quantity;
    private double Price;
    private double Total;
    private String UsedDate;

    public int getUsageID() { return UsageID; }
    public String getServiceName() { return ServiceName; }
    public int getQuantity() { return Quantity; }
    public double getPrice() { return Price; }
    public double getTotal() { return Total; }
    public String getUsedDate() { return UsedDate; }
}
