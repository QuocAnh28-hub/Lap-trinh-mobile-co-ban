package com.example.btl_datphongkhachsan.models;

public class RoomStayHistory {
    private String STT;
    private int ID;
    private int RoomID;
    private String SoPhong;
    private String RoomType;
    private String CheckInTime;
    private String CheckOutTime;
    private double RateAtThatTime;
    private double Amount;

    public String getSTT() { return STT; }
    public int getID() { return ID; }
    public int getRoomID() { return RoomID; }
    public String getSoPhong() { return SoPhong; }
    public String getRoomType() { return RoomType; }
    public String getCheckInTime() { return CheckInTime; }
    public String getCheckOutTime() { return CheckOutTime; }
    public double getRateAtThatTime() { return RateAtThatTime; }
    public double getAmount() { return Amount; }
}
