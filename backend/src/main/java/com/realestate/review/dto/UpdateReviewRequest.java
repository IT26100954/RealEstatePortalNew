package com.realestate.review.dto;

import java.math.BigDecimal;

public class UpdateReviewRequest {
    private Integer rating;
    private String comment;

    public UpdateReviewRequest() {
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        // Reject fractional JSON ratings instead of silently rounding them.
        try {
            this.rating = rating == null ? null : rating.intValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Rating must be a whole number.");
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Reject IDs, identities and other fields not allowed by this DTO.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}
