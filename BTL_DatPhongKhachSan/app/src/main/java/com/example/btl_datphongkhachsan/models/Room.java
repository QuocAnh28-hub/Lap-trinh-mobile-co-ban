package com.example.btl_datphongkhachsan.models;

public class Room {
    private int RoomID;
    private String RoomNumber;
    private String Status;
    private int RoomTypeID;
    private String RoomTypeName;
    private String Description;
    private int Capacity;
    private double DefaultPrice;

    public int getRoomID() { return RoomID; }
    public String getRoomNumber() { return RoomNumber; }
    public String getStatus() { return Status; }
    public void setStatus(String Status) { this.Status = Status; }
    public int getRoomTypeID() { return RoomTypeID; }
    public String getRoomTypeName() { return RoomTypeName; }
    public String getDescription() { return Description; }
    public int getCapacity() { return Capacity; }
    public double getDefaultPrice() { return DefaultPrice; }

    @Override
    public String toString() {
        if (RoomTypeName != null && !RoomTypeName.isEmpty()) {
            return RoomNumber + " (" + RoomTypeName + ")";
        }
        return "Phòng " + RoomNumber;
    }
}
