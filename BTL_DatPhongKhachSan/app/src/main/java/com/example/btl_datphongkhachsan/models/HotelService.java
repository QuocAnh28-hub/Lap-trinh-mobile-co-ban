package com.example.btl_datphongkhachsan.models;

import java.util.Locale;

public class HotelService {
    private int ServiceID;
    private String ServiceName;
    private double Price;
    private String Status;

    public int getServiceID() {
        return ServiceID;
    }

    public void setServiceID(int serviceID) {
        ServiceID = serviceID;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s - %,.0fđ", ServiceName, Price);
    }
}
