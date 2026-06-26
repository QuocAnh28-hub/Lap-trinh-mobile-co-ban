package com.example.btl_datphongkhachsan.models;

public class AvailableRoom {
    private int RoomID;
    private String RoomNumber;
    private String RoomType;

    public int getRoomID() {
        return RoomID;
    }

    public void setRoomID(int roomID) {
        RoomID = roomID;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String roomType) {
        RoomType = roomType;
    }

    @Override
    public String toString() {
        return RoomNumber + " (" + RoomType + ")";
    }
}
