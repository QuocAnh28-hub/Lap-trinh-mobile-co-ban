package com.example.btl_datphongkhachsan.models;

public class CreateInvoiceResponse {
    private int InvoiceID;
    private double SubTotal;
    private double VATAmount;
    private double Total;

    public int getInvoiceID() { return InvoiceID; }
    public double getSubTotal() { return SubTotal; }
    public double getVATAmount() { return VATAmount; }
    public double getTotal() { return Total; }
}
