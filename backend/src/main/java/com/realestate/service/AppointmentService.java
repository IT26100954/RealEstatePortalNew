
package com.appoiment.demoviewing_appointments.service;

import com.appoiment.demoviewing_appointments.model.Appointment;
import com.appoiment.demoviewing_appointments.model.AppointmentStatus;
import com.appoiment.demoviewing_appointments.repository.AppointmentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // CREATE
    public Appointment createAppointment(Appointment appointment) {

        validateAppointment(appointment);

        // Check whether the requested time slot is already occupied
        validateAppointmentSlot(appointment, null);

        appointment.setId(UUID.randomUUID().toString());
        appointment.setStatus(AppointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

    // READ: Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // READ: Find appointment by ID
    public Appointment getAppointmentById(String id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Appointment not found: " + id));
    }

    // UPDATE
    public Appointment updateAppointment(
            String id, Appointment updatedAppointment) {

        Appointment existing = getAppointmentById(id);

        // Preserve the existing ID and status
        updatedAppointment.setId(id);
        updatedAppointment.setStatus(existing.getStatus());

        validateAppointment(updatedAppointment);

        // Exclude this appointment from the conflict check
        validateAppointmentSlot(updatedAppointment, id);

        boolean updated =
                appointmentRepository.update(updatedAppointment);

        if (!updated) {
            throw new IllegalStateException(
                    "Could not update appointment.");
        }

        return updatedAppointment;
    }

    // DELETE
    public void deleteAppointment(String id) {

        boolean deleted = appointmentRepository.deleteById(id);

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Appointment not found: " + id);
        }
    }

    // BASIC VALIDATION
    private void validateAppointment(Appointment appointment) {

        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Appointment data is required.");
        }

        if (isBlank(appointment.getPropertyId())
                || isBlank(appointment.getBuyerId())
                || isBlank(appointment.getAgentId())) {

            throw new IllegalArgumentException(
                    "Property, buyer and agent IDs are required.");
        }

        if (appointment.getViewDate() == null
                || appointment.getViewTime() == null) {

            throw new IllegalArgumentException(
                    "Viewing date and time are required.");
        }

        if (appointment.getViewDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Viewing date cannot be in the past.");
        }

        if (appointment.getViewType() == null) {
            throw new IllegalArgumentException(
                    "Viewing type is required.");
        }
    }

    // DUPLICATE / TIME-SLOT VALIDATION
    private void validateAppointmentSlot(
            Appointment appointment, String excludedAppointmentId) {

        List<Appointment> existingAppointments =
                appointmentRepository.findAll();

        for (Appointment existing : existingAppointments) {

            // Skip the same appointment when editing
            if (excludedAppointmentId != null
                    && excludedAppointmentId.equals(existing.getId())) {
                continue;
            }

            // Cancelled and rejected appointments do not reserve a slot
            if (existing.getStatus() == AppointmentStatus.CANCELLED
                    || existing.getStatus() == AppointmentStatus.REJECTED) {
                continue;
            }

            // Ignore incomplete records, if any
            if (existing.getViewDate() == null
                    || existing.getViewTime() == null) {
                continue;
            }

            // A conflict requires the same date AND time
            boolean sameDateAndTime =
                    existing.getViewDate().equals(appointment.getViewDate())
                    && existing.getViewTime().equals(appointment.getViewTime());

            if (!sameDateAndTime) {
                continue;
            }

            // Rule 1: Same buyer cannot book two appointments at once
            if (sameValue(
                    existing.getBuyerId(),
                    appointment.getBuyerId())) {

                throw new IllegalArgumentException(
                        "This buyer already has an appointment "
                        + "at this date and time. Please select another time.");
            }

            // Rule 2: Same agent cannot handle two appointments at once
            if (sameValue(
                    existing.getAgentId(),
                    appointment.getAgentId())) {

                throw new IllegalArgumentException(
                        "This agent already has an appointment "
                        + "at this date and time. Please select another time.");
            }

            // Rule 3: Same property cannot be booked twice at once
            if (sameValue(
                    existing.getPropertyId(),
                    appointment.getPropertyId())) {

                throw new IllegalArgumentException(
                        "This property already has an appointment "
                        + "at this date and time. Please select another time.");
            }
        }
    }

    private boolean sameValue(String first, String second) {

        return first != null
                && second != null
                && first.trim().equalsIgnoreCase(second.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // Get appointments belonging to a specific buyer
    public List<Appointment> getAppointmentsForBuyer(String buyerId) {

        return appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getBuyerId() != null
                        && a.getBuyerId().equalsIgnoreCase(buyerId))
                .toList();
    }

    // Get appointments assigned to a specific agent
    public List<Appointment> getAppointmentsForAgent(String agentId) {

        return appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getAgentId() != null
                        && a.getAgentId().equalsIgnoreCase(agentId))
                .toList();
    }

    // Update appointment status
    public Appointment updateAppointmentStatus(
            String id, AppointmentStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Appointment status is required.");
        }

        Appointment appointment = getAppointmentById(id);

        appointment.setStatus(newStatus);

        boolean updated = appointmentRepository.update(appointment);

        if (!updated) {
            throw new IllegalStateException(
                    "Could not update appointment status.");
        }

        return appointment;
    }
}
