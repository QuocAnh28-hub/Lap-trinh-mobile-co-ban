package com.example.btl_datphongkhachsan.models;

public class SearchAvailableRequest {
    private String CheckInDate;
    private String CheckOutDate;
    private int NumPeople;
    private int NumRooms;

    public SearchAvailableRequest(String checkInDate, String checkOutDate, int numPeople, int numRooms) {
        this.CheckInDate = checkInDate;
        this.CheckOutDate = checkOutDate;
        this.NumPeople = numPeople;
        this.NumRooms = numRooms;
    }
}