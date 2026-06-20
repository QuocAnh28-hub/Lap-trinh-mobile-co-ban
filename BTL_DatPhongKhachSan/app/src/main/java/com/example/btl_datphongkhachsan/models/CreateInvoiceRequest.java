package com.example.btl_datphongkhachsan.models;

public class CreateInvoiceRequest {
    private int StayID;
    private String Method;
    private int VAT;

    public CreateInvoiceRequest(int stayID, String method, int VAT) {
        this.StayID = stayID;
        this.Method = method;
        this.VAT = VAT;
    }

    public int getStayID() { return StayID; }
    public String getMethod() { return Method; }
    public int getVAT() { return VAT; }
}
