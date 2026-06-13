package com.example.btl_datphongkhachsan.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RoomType implements Serializable {
    @SerializedName("RoomTypeID")
    private int RoomTypeID;
    
    @SerializedName("Name")
    private String Name;
    
    @SerializedName("Description")
    private String Description;
    
    @SerializedName("Capacity")
    private int Capacity;
    
    @SerializedName("DefaultPrice")
    private double DefaultPrice;
    
    @SerializedName("Price")
    private double Price;
    
    @SerializedName("TotalRooms")
    private int TotalRooms;
    
    @SerializedName("ReservedRooms")
    private int ReservedRooms;
    
    @SerializedName("OccupiedRooms")
    private int OccupiedRooms;
    
    @SerializedName("AvailableRooms")
    private int AvailableRooms;

    public int getRoomTypeID() { return RoomTypeID; }
    public String getName() { return Name; }
    public String getDescription() { return Description; }
    public int getCapacity() { return Capacity; }
    
    public double getDisplayPrice() {
        return Price > 0 ? Price : DefaultPrice;
    }

    public double getDefaultPrice() { return DefaultPrice; }
    public double getPrice() { return Price; }
    public int getTotalRooms() { return TotalRooms; }
    public int getAvailableRooms() { return AvailableRooms; }
}