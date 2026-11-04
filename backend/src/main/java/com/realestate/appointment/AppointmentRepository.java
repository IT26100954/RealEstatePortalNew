package com.realestate.appointment;

import java.util.List;

public interface AppointmentRepository {
    List<Appointment> findAll();
    Appointment findById(String id);
    Appointment lockById(String id);
    void insert(Appointment record);
    void update(Appointment record);
    void delete(String id);
    int countConfirmedSlot(String propertyId, java.time.LocalDate date, java.time.LocalTime time, String excludedId);
}
