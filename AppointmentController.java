package com.realestate.appointment;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CurrentUser;
import com.realestate.appointment.dto.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;
    private final CurrentUser currentUser;
    public AppointmentController(AppointmentService service, CurrentUser currentUser) {
        this.service=service;
        this.currentUser=currentUser;
    }
    @GetMapping
    //Read
    public List<AppointmentResponse> list(HttpSession session, @RequestParam(required=false) String q, @RequestParam(required=false) String city) {
        return service.list(currentUser.required(session),q,city);
    }
    @GetMapping("/{id}")
    //Detail
    public AppointmentResponse detail(@PathVariable String id, HttpSession session) {
        return service.detail(id,currentUser.required(session));
    }
    //Create
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@RequestBody CreateAppointmentRequest request, HttpSession session) {
        return service.create(request,currentUser.required(session));
    }
    @PutMapping("/{id}")
    //Upadate
    public AppointmentResponse update(@PathVariable String id,@RequestBody UpdateAppointmentRequest request,HttpSession session) {
        return service.update(id,request,currentUser.required(session));
    }
    //Delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,HttpSession session) { service.delete(id,currentUser.required(session)); }
    //Status Change
    @PatchMapping("/{id}/status")
    public AppointmentResponse status(@PathVariable String id,@RequestBody AppointmentStatusRequest request,HttpSession session) {
        return service.changeStatus(id,request.getStatus(),currentUser.required(session));
    }
}
