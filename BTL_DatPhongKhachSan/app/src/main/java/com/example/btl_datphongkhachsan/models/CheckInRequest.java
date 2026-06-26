package com.example.btl_datphongkhachsan.models;

public class CheckInRequest {
    private int ReservationID;
    private int RoomID;

    public CheckInRequest(int reservationID, int roomID) {
        ReservationID = reservationID;
        RoomID = roomID;
    }

    public int getReservationID() {
        return ReservationID;
    }

    public void setReservationID(int reservationID) {
        ReservationID = reservationID;
    }

    public int getRoomID() {
        return RoomID;
    }

    public void setRoomID(int roomID) {
        RoomID = roomID;
    }
}
