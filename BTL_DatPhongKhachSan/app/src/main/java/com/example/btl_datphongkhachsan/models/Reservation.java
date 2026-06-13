package com.example.btl_datphongkhachsan.models;

import java.io.Serializable;

public class Reservation implements Serializable {
    private int StayID;
    private int ReservationID;
    private String Status;
    private String CheckIn;
    private String CheckOut;
    private String RoomType;
    private int RoomTypeID;
    private int Quantity;
    private double TotalAmount;

    public int getStayID() { return StayID; }
    public void setStayID(int stayID) { StayID = stayID; }

    public int getReservationID() { return ReservationID; }
    public void setReservationID(int reservationID) { ReservationID = reservationID; }

    public String getStatus() { return Status; }
    public void setStatus(String status) { Status = status; }

    public String getCheckIn() { return CheckIn; }
    public void setCheckIn(String checkIn) { CheckIn = checkIn; }

    public String getCheckOut() { return CheckOut; }
    public void setCheckOut(String checkOut) { CheckOut = checkOut; }

    public String getRoomType() { return RoomType; }
    public void setRoomType(String roomType) { RoomType = roomType; }

    public int getRoomTypeID() { return RoomTypeID; }
    public void setRoomTypeID(int roomTypeID) { RoomTypeID = roomTypeID; }

    public int getQuantity() { return Quantity; }
    public void setQuantity(int quantity) { Quantity = quantity; }

    public double getTotalAmount() { return TotalAmount; }
    public void setTotalAmount(double totalAmount) { TotalAmount = totalAmount; }
}