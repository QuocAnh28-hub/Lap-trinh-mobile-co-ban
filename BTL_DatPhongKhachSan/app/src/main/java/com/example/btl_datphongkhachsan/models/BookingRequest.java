package com.example.btl_datphongkhachsan.models;

public class BookingRequest {
    private String UserID;
    private int RoomTypeID;
    private String CheckInDate;
    private String CheckOutDate;
    private int NumRooms;
    private int NumPeople;

    public BookingRequest(String userID, int roomTypeID, String checkInDate, String checkOutDate, int numRooms, int numPeople) {
        this.UserID = userID;
        this.RoomTypeID = roomTypeID;
        this.CheckInDate = checkInDate;
        this.CheckOutDate = checkOutDate;
        this.NumRooms = numRooms;
        this.NumPeople = numPeople;
    }
}