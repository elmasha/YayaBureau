package com.intech.yayabureau.Models;

public class ResultStk {

    private String CustomerMessage;
    private String CheckoutRequestID;
    private Integer ResultCode;
    private String ResultDesc;

    public String getCustomerMessage() {
        return CustomerMessage;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public Integer getResultCode() {
        return ResultCode;
    }

    public String getResultDesc() {
        return ResultDesc;
    }
}
