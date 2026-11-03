package com.realestate.appointment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateAppointmentRequest {
    private LocalDate viewingDate;
    private LocalTime viewingTime;
    private String notes;

    public UpdateAppointmentRequest() { }
    public LocalDate getViewingDate() { return viewingDate; }
    public void setViewingDate(LocalDate viewingDate) { this.viewingDate=viewingDate; }
    public LocalTime getViewingTime() { return viewingTime; }
    public void setViewingTime(LocalTime viewingTime) { this.viewingTime=viewingTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes=notes; }
    // Reject IDs, identities and other fields not allowed by this DTO.
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void rejectUnknown(String field, Object value) {
        throw new IllegalArgumentException("Unsupported request field: " + field);
    }
}
