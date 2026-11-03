package com.realestate.offer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class OfferResponse {
    private String id;
    private String buyerId;
    private String propertyId;
    private String amount;
    private String message;
    private String status;

    public OfferResponse() { }
    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId=buyerId; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId=propertyId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount=amount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message=message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status=status; }
}
