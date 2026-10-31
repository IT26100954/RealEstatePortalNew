
package com.appoiment.demoviewing_appointments.controller;

import com.appoiment.demoviewing_appointments.model.Appointment;
import com.appoiment.demoviewing_appointments.model.AppointmentStatus;
import com.appoiment.demoviewing_appointments.model.ViewType;
import com.appoiment.demoviewing_appointments.service.AppointmentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // READ: Redirect appointments page to Buyer Dashboard
    @GetMapping
    public String listAppointments() {
        return "redirect:/dashboard?role=BUYER";
    }

    // CREATE: Show appointment form
    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute("appointment", new Appointment());
        model.addAttribute("viewTypes", ViewType.values());
        model.addAttribute(
                "formTitle", "Create Viewing Appointment");
        model.addAttribute("formAction", "/appointments");
        model.addAttribute("isEdit", false);

        return "appointment-form";
    }

    // CREATE: Save a new appointment
    @PostMapping
    public String createAppointment(
            @ModelAttribute("appointment") Appointment appointment,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            appointmentService.createAppointment(appointment);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Appointment created successfully!");

            return "redirect:/dashboard?role=BUYER";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("appointment", appointment);
            model.addAttribute("viewTypes", ViewType.values());
            model.addAttribute(
                    "formTitle", "Create Viewing Appointment");
            model.addAttribute("formAction", "/appointments");
            model.addAttribute("isEdit", false);
            model.addAttribute("errorMessage", ex.getMessage());

            return "appointment-form";
        }
    }

    // UPDATE: Display the existing appointment
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable String id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Appointment appointment =
                    appointmentService.getAppointmentById(id);

            model.addAttribute("appointment", appointment);
            model.addAttribute("viewTypes", ViewType.values());
            model.addAttribute(
                    "formTitle", "Reschedule Appointment");
            model.addAttribute(
                    "formAction", "/appointments/" + id);
            model.addAttribute("isEdit", true);

            return "appointment-form";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());

            return "redirect:/dashboard?role=BUYER";
        }
    }

    // UPDATE: Save edited appointment
    @PostMapping("/{id}")
    public String updateAppointment(
            @PathVariable String id,
            @ModelAttribute("appointment") Appointment updated,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Appointment existing =
                    appointmentService.getAppointmentById(id);

            // Keep the existing status when editing
            updated.setStatus(existing.getStatus());

            appointmentService.updateAppointment(id, updated);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Appointment updated successfully!");

            return "redirect:/dashboard?role=BUYER";

        } catch (IllegalArgumentException | IllegalStateException ex) {

            updated.setId(id);

            model.addAttribute("appointment", updated);
            model.addAttribute("viewTypes", ViewType.values());
            model.addAttribute(
                    "formTitle", "Reschedule Appointment");
            model.addAttribute(
                    "formAction", "/appointments/" + id);
            model.addAttribute("isEdit", true);
            model.addAttribute("errorMessage", ex.getMessage());

            return "appointment-form";
        }
    }

    // DELETE: Remove an appointment
    @PostMapping("/{id}/delete")
    public String deleteAppointment(
            @PathVariable String id,
            RedirectAttributes redirectAttributes) {

        try {
            appointmentService.deleteAppointment(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Appointment deleted successfully!");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
        }

        return "redirect:/dashboard?role=BUYER";
    }

    // AGENT: Confirm or reject PENDING appointments only
    @PostMapping("/{id}/status")
    public String updateAppointmentStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(defaultValue = "BUYER") String role,
            RedirectAttributes redirectAttributes) {

        // Demo role check
        if (!"AGENT".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Only agents can review appointments.");

            return "redirect:/dashboard?role=BUYER";
        }

        try {
            // Validate requested status first
            AppointmentStatus newStatus;

            try {
                newStatus = AppointmentStatus.valueOf(
                        status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Invalid appointment status.");

                return "redirect:/dashboard?role=AGENT";
            }

            // Only CONFIRMED or REJECTED are allowed
            if (newStatus != AppointmentStatus.CONFIRMED
                    && newStatus != AppointmentStatus.REJECTED) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Only Confirm or Reject actions are allowed.");

                return "redirect:/dashboard?role=AGENT";
            }

            Appointment appointment =
                    appointmentService.getAppointmentById(id);

            // Only PENDING appointments can be reviewed
            if (appointment.getStatus() != AppointmentStatus.PENDING) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "This appointment has already been reviewed. "
                        + "Only pending appointments can be reviewed.");

                return "redirect:/dashboard?role=AGENT";
            }

            // Save the new status
            appointmentService.updateAppointmentStatus(id, newStatus);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Appointment status updated to " + newStatus + "!");

        } catch (IllegalArgumentException | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
        }

        return "redirect:/dashboard?role=AGENT";
    }

    // BUYER: Cancel an appointment
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(
            @PathVariable String id,
            @RequestParam String buyerId,
            RedirectAttributes redirectAttributes) {

        try {
            Appointment appointment =
                    appointmentService.getAppointmentById(id);

            // Verify buyer ID matches the appointment
            if (appointment.getBuyerId() == null
                    || !appointment.getBuyerId()
                            .equalsIgnoreCase(buyerId)) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "This appointment does not belong to this buyer.");

                return "redirect:/dashboard?role=BUYER";
            }

            // Only pending or confirmed appointments can be cancelled
            if (appointment.getStatus() != AppointmentStatus.PENDING
                    && appointment.getStatus()
                            != AppointmentStatus.CONFIRMED) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "This appointment cannot be cancelled.");

                return "redirect:/dashboard?role=BUYER";
            }

            appointmentService.updateAppointmentStatus(
                    id, AppointmentStatus.CANCELLED);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Appointment cancelled successfully!");

        } catch (IllegalArgumentException | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
        }

        return "redirect:/dashboard?role=BUYER";
    }
}
