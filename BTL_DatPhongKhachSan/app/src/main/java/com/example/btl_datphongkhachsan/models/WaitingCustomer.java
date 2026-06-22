package com.example.btl_datphongkhachsan.models;

public class WaitingCustomer {
    private int ReservationID;
    private int UserID;
    private int CustomerID;
    private String FullName;
    private String Phone;
    private String Email;
    private String CheckInDate;
    private String CheckOutDate;
    private String Name;
    private int RoomTypeID;
    private double PriceAtBooking;

    public int getReservationID() { return ReservationID; }
    public int getUserID() { return UserID; }
    public int getCustomerID() { return CustomerID; }
    public String getFullName() { return FullName; }
    public String getPhone() { return Phone; }
    public String getEmail() { return Email; }
    public String getCheckInDate() { return CheckInDate; }
    public String getCheckOutDate() { return CheckOutDate; }
    public String getName() { return Name; }
    public int getRoomTypeID() { return RoomTypeID; }
    public double getPriceAtBooking() { return PriceAtBooking; }
}
