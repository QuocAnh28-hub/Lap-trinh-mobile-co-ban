package com.example.btl_datphongkhachsan.models;

public class PendingInvoice {
    private int StayID;
    private String GuestName;
    private String IdentityNumber;
    private String ActualCheckIn;
    private String ActualCheckOut;
    private double RoomCharge;
    private double ServiceCharge;
    private double MinibarCharge;
    private double PenaltyCharge;
    private double TotalAmount;

    public int getStayID() { return StayID; }
    public String getGuestName() { return GuestName; }
    public String getIdentityNumber() { return IdentityNumber; }
    public String getActualCheckIn() { return ActualCheckIn; }
    public String getActualCheckOut() { return ActualCheckOut; }
    public double getRoomCharge() { return RoomCharge; }
    public double getServiceCharge() { return ServiceCharge; }
    public double getMinibarCharge() { return MinibarCharge; }
    public double getPenaltyCharge() { return PenaltyCharge; }
    public double getTotalAmount() { return TotalAmount; }
}
