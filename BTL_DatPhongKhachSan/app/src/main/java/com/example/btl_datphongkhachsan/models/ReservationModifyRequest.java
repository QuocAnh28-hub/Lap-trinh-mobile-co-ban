package com.example.btl_datphongkhachsan.models;

public class ReservationModifyRequest {
    private int RoomTypeID;
    private int Quantity;
    private String CheckInDate;
    private String CheckOutDate;

    public ReservationModifyRequest(int roomTypeID, int quantity, String checkInDate, String checkOutDate) {
        this.RoomTypeID = roomTypeID;
        this.Quantity = quantity;
        this.CheckInDate = checkInDate;
        this.CheckOutDate = checkOutDate;
    }
}
