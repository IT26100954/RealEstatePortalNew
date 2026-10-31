
package com.appoiment.demoviewing_appointments.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private String id;
    private String propertyId;
    private String buyerId;
    private String agentId;

    private LocalDate viewDate;
    private LocalTime viewTime;

    private ViewType viewType;
    private AppointmentStatus status;
    private String note;

    // Default constructor
    public Appointment() {
    }

    // Parameterized constructor
    public Appointment(
            String id,
            String propertyId,
            String buyerId,
            String agentId,
            LocalDate viewDate,
            LocalTime viewTime,
            ViewType viewType,
            AppointmentStatus status,
            String note) {

        this.id = id;
        this.propertyId = propertyId;
        this.buyerId = buyerId;
        this.agentId = agentId;
        this.viewDate = viewDate;
        this.viewTime = viewTime;
        this.viewType = viewType;
        this.status = status;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public LocalDate getViewDate() {
        return viewDate;
    }

    public void setViewDate(LocalDate viewDate) {
        this.viewDate = viewDate;
    }

    public LocalTime getViewTime() {
        return viewTime;
    }

    public void setViewTime(LocalTime viewTime) {
        this.viewTime = viewTime;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
