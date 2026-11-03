package com.realestate.appointment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentResponse {
    private String id;
    private String buyerId;
    private String propertyId;
    private LocalDate viewingDate;
    private LocalTime viewingTime;
    private String status;
    private String notes;

    public AppointmentResponse() { }
    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId=buyerId; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId=propertyId; }
    public LocalDate getViewingDate() { return viewingDate; }
    public void setViewingDate(LocalDate viewingDate) { this.viewingDate=viewingDate; }
    public LocalTime getViewingTime() { return viewingTime; }
    public void setViewingTime(LocalTime viewingTime) { this.viewingTime=viewingTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status=status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes=notes; }
}
