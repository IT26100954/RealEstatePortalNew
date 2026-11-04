package com.realestate.appointment;

import java.util.List;
import com.realestate.common.CurrentAccount;
import com.realestate.appointment.dto.*;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest request, CurrentAccount actor);
    AppointmentResponse detail(String id, CurrentAccount actor);
    List<AppointmentResponse> list(CurrentAccount actor, String q, String city);
    AppointmentResponse update(String id, UpdateAppointmentRequest request, CurrentAccount actor);
    void delete(String id, CurrentAccount actor);
    AppointmentResponse changeStatus(String id, String status, CurrentAccount actor);
}
