package com.realestate.offer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateOfferRequest {
    private String propertyId;
    private String amount;
    private String message;

    public CreateOfferRequest() { }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId=propertyId; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount=amount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message=message; }
    // Reject IDs, identities and other fields not allowed by this DTO.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}
