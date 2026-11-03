package com.realestate.review.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateReviewRequest {
    private String propertyId;
    private BigDecimal rating;
    private String comment;

    public CreateReviewRequest() { }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId=propertyId; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating=rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment=comment; }
    // Reject IDs, identities and other fields not allowed by this DTO.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}
