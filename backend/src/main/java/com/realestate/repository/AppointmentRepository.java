
package com.appoiment.demoviewing_appointments.repository;

import com.appoiment.demoviewing_appointments.model.Appointment;
import com.appoiment.demoviewing_appointments.model.AppointmentStatus;
import com.appoiment.demoviewing_appointments.model.ViewType;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepository {

    private final Path filePath =
            Path.of("data", "appointments.txt").toAbsolutePath();

    public AppointmentRepository() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not initialize appointments file.", e);
        }
    }

    // CREATE: Save a new appointment
    public synchronized Appointment save(Appointment appointment) {
        if (appointment == null
                || appointment.getId() == null
                || appointment.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "Appointment and appointment ID are required.");
        }

        if (findById(appointment.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "An appointment with this ID already exists.");
        }

        List<String> lines = readLines();
        lines.add(toLine(appointment));
        writeLines(lines);

        return appointment;
    }

    // READ: Get all appointments
    public synchronized List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();

        for (String line : readLines()) {
            if (line.isBlank()) {
                continue;
            }

            appointments.add(fromLine(line));
        }

        return appointments;
    }

    // READ: Find an appointment by ID
    public synchronized Optional<Appointment> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        return findAll().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    // UPDATE: Replace an existing appointment
    public synchronized boolean update(Appointment updated) {
        if (updated == null
                || updated.getId() == null
                || updated.getId().isBlank()) {
            return false;
        }

        List<String> lines = readLines();
        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            Appointment current = fromLine(line);

            if (current.getId().equals(updated.getId())) {
                lines.set(i, toLine(updated));
                found = true;
                break;
            }
        }

        if (found) {
            writeLines(lines);
        }

        return found;
    }

    // DELETE: Remove an appointment by ID
    public synchronized boolean deleteById(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        List<String> lines = readLines();
        boolean removed = lines.removeIf(line -> {
            if (line.isBlank()) {
                return false;
            }

            return fromLine(line).getId().equals(id);
        });

        if (removed) {
            writeLines(lines);
        }

        return removed;
    }

    // Read all lines from the file
    private List<String> readLines() {
        try {
            return Files.readAllLines(
                    filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not read appointments file.", e);
        }
    }

    // Write all lines back to the file
    private void writeLines(List<String> lines) {
        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not write appointments file.", e);
        }
    }

    // Convert an appointment into one text-file line
    private String toLine(Appointment a) {
        return String.join("\t",
                encode(a.getId()),
                encode(a.getPropertyId()),
                encode(a.getBuyerId()),
                encode(a.getAgentId()),
                a.getViewDate().toString(),
                a.getViewTime().toString(),
                a.getViewType().name(),
                a.getStatus().name(),
                encode(a.getNote())
        );
    }

    // Convert one text-file line back into an appointment
    private Appointment fromLine(String line) {
        String[] fields = line.split("\t", -1);

        if (fields.length != 9) {
            throw new IllegalStateException(
                    "Invalid appointment record in file.");
        }

        return new Appointment(
                decode(fields[0]),
                decode(fields[1]),
                decode(fields[2]),
                decode(fields[3]),
                LocalDate.parse(fields[4]),
                LocalTime.parse(fields[5]),
                ViewType.valueOf(fields[6]),
                AppointmentStatus.valueOf(fields[7]),
                decode(fields[8])
        );
    }

    // Encode text so tabs or special characters won't break records
    private String encode(String value) {
        if (value == null) {
            return "";
        }

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        if (value.isEmpty()) {
            return "";
        }

        byte[] bytes = Base64.getUrlDecoder().decode(value);

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
