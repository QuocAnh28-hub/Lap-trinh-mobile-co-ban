package com.example.btl_datphongkhachsan.models;

public class StayingCustomer {
    private int StayID;
    private Integer ReservationID;
    private int CustomerID;
    private String FullName;
    private String Phone;
    private String Email;
    private String CheckInDate;
    private String CheckOutDate;
    private int RoomID;
    private String RoomNumber;
    private String Status;
    private String CreatedAt;

    public int getStayID() { return StayID; }
    public Integer getReservationID() { return ReservationID; }
    public int getCustomerID() { return CustomerID; }
    public String getFullName() { return FullName; }
    public String getPhone() { return Phone; }
    public String getEmail() { return Email; }
    public String getCheckInDate() { return CheckInDate; }
    public String getCheckOutDate() { return CheckOutDate; }
    public int getRoomID() { return RoomID; }
    public String getRoomNumber() { return RoomNumber; }
    public String getStatus() { return Status; }
    public String getCreatedAt() { return CreatedAt; }
}
