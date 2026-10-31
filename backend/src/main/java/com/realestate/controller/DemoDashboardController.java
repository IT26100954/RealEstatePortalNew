
package com.appoiment.demoviewing_appointments.controller;

import com.appoiment.demoviewing_appointments.model.Appointment;
import com.appoiment.demoviewing_appointments.model.AppointmentStatus;
import com.appoiment.demoviewing_appointments.model.UserRole;
import com.appoiment.demoviewing_appointments.service.AppointmentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DemoDashboardController {

    private final AppointmentService appointmentService;

    public DemoDashboardController(
            AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String showRoleSelector() {
        return "role-selector";
    }

    @GetMapping("/dashboard")
    public String showDashboard(
            @RequestParam(defaultValue = "BUYER") String role,
            Model model) {

        UserRole selectedRole;

        try {
            selectedRole = UserRole.valueOf(
                    role.trim().toUpperCase());
        } catch (IllegalArgumentException
                 | NullPointerException ex) {
            selectedRole = UserRole.BUYER;
        }

        // Read all appointments from text-file storage
        List<Appointment> allAppointments =
                appointmentService.getAllAppointments();

        model.addAttribute("role", selectedRole.name());

        switch (selectedRole) {

            case BUYER: {
                // Buyer dashboard displays ALL buyers' appointments
                model.addAttribute("pageTitle",
                        "Viewing Appointments");

                model.addAttribute("subtitle",
                        "View all property viewing appointments.");

                model.addAttribute("appointments",
                        allAppointments);

                updateStatistics(model, allAppointments);
                break;
            }

            case AGENT: {
                // Agent can review all appointments in this demo
                model.addAttribute("pageTitle",
                        "Agent Workspace");

                model.addAttribute("subtitle",
                        "Review and manage all viewing appointments.");

                model.addAttribute("appointments",
                        allAppointments);

                updateStatistics(model, allAppointments);
                break;
            }

            case ADMIN: {
                // Admin sees all appointments
                model.addAttribute("pageTitle",
                        "Admin Dashboard");

                model.addAttribute("subtitle",
                        "Overview of viewing appointment activity.");

                model.addAttribute("appointments",
                        allAppointments);

                updateStatistics(model, allAppointments);
                break;
            }
        }

        return "dashboard";
    }

    // Update statistics using the appointments shown on the dashboard
    private void updateStatistics(
            Model model,
            List<Appointment> appointments) {

        model.addAttribute("totalAppointments",
                appointments.size());

        model.addAttribute("pendingCount",
                countByStatus(
                        appointments,
                        AppointmentStatus.PENDING));

        model.addAttribute("confirmedCount",
                countByStatus(
                        appointments,
                        AppointmentStatus.CONFIRMED));

        model.addAttribute("cancelledCount",
                countByStatus(
                        appointments,
                        AppointmentStatus.CANCELLED));
    }

    // Count appointments by status
    private long countByStatus(
            List<Appointment> appointments,
            AppointmentStatus status) {

        return appointments.stream()
                .filter(a -> a.getStatus() == status)
                .count();
    }
}
