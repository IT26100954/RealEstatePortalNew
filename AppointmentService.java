package com.realestate.appointment;

import java.util.List;
import com.realestate.common.CurrentAccount;
import com.realestate.appointment.dto.*;

public interface AppointmentService {
    //Creat
    AppointmentResponse create(CreateAppointmentRequest request, CurrentAccount actor);
    //Detail
    AppointmentResponse detail(String id, CurrentAccount actor);
    //Read
    List<AppointmentResponse> list(CurrentAccount actor, String q, String city);
    //Upadate
    AppointmentResponse update(String id, UpdateAppointmentRequest request, CurrentAccount actor);
    //Delete
    void delete(String id, CurrentAccount actor);
    //Status Change
    AppointmentResponse changeStatus(String id, String status, CurrentAccount actor);
}
