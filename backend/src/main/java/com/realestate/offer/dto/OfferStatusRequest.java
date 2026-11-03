package com.realestate.offer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class OfferStatusRequest {
    private String status;

    public OfferStatusRequest() { }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status=status; }
    // Reject IDs, identities and other fields not allowed by this DTO.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}
