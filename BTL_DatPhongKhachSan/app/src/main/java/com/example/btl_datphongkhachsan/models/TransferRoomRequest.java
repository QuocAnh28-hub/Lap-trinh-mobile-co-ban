package com.example.btl_datphongkhachsan.models;

public class TransferRoomRequest {
    private int StayID;
    private int OldRoomID;
    private int NewRoomID;

    public TransferRoomRequest(int stayID, int oldRoomID, int newRoomID) {
        StayID = stayID;
        OldRoomID = oldRoomID;
        NewRoomID = newRoomID;
    }

    public int getStayID() {
        return StayID;
    }

    public void setStayID(int stayID) {
        StayID = stayID;
    }

    public int getOldRoomID() {
        return OldRoomID;
    }

    public void setOldRoomID(int oldRoomID) {
        OldRoomID = oldRoomID;
    }

    public int getNewRoomID() {
        return NewRoomID;
    }

    public void setNewRoomID(int newRoomID) {
        NewRoomID = newRoomID;
    }
}
