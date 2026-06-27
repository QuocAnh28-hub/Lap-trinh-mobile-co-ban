package com.example.btl_datphongkhachsan.models;

import java.util.Locale;

public class MinibarItem {
    private int MiniBarID;
    private String ItemName;
    private double Price;

    public int getMiniBarID() {
        return MiniBarID;
    }

    public void setMiniBarID(int miniBarID) {
        MiniBarID = miniBarID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s - %,.0fđ", ItemName, Price);
    }
}
