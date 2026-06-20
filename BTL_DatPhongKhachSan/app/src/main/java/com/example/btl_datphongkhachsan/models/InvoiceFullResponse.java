package com.example.btl_datphongkhachsan.models;

import java.util.List;

public class InvoiceFullResponse {
    private Invoice invoice;
    private List<Detail> details;

    public Invoice getInvoice() { return invoice; }
    public List<Detail> getDetails() { return details; }

    public static class Invoice {
        private int InvoiceID;
        private int StayID;
        private String Date;
        private double TotalAmount;
        private double VAT;
        private String ActualCheckIn;
        private String ActualCheckOut;
        private String FullName;
        private String Phone;
        private String Email;

        public int getInvoiceID() { return InvoiceID; }
        public int getStayID() { return StayID; }
        public String getDate() { return Date; }
        public double getTotalAmount() { return TotalAmount; }
        public double getVAT() { return VAT; }
        public String getActualCheckIn() { return ActualCheckIn; }
        public String getActualCheckOut() { return ActualCheckOut; }
        public String getFullName() { return FullName; }
        public String getPhone() { return Phone; }
        public String getEmail() { return Email; }
    }

    public static class Detail {
        private int DetailID;
        private int InvoiceID;
        private String ItemType;
        private String ItemName;
        private int Quantity;
        private double UnitPrice;
        private double Amount;

        public int getDetailID() { return DetailID; }
        public int getInvoiceID() { return InvoiceID; }
        public String getItemType() { return ItemType; }
        public String getItemName() { return ItemName; }
        public int getQuantity() { return Quantity; }
        public double getUnitPrice() { return UnitPrice; }
        public double getAmount() { return Amount; }
    }
}
