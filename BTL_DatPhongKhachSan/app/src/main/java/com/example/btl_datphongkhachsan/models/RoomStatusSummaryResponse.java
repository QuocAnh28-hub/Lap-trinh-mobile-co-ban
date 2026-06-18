package com.example.btl_datphongkhachsan.models;

public class RoomStatusSummaryResponse {
    private int AvailableRooms;
    private int OccupiedRooms;
    private int DirtyRooms;

    public int getAvailableRooms() { return AvailableRooms; }
    public int getOccupiedRooms() { return OccupiedRooms; }
    public int getDirtyRooms() { return DirtyRooms; }
}
