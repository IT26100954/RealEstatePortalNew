package com.realestate.appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.realestate.common.*;
import com.realestate.appointment.dto.*;
import static com.realestate.common.InputRules.*;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository repository;
    private final PropertyLookup properties;
    public AppointmentServiceImpl(AppointmentRepository repository, PropertyLookup properties) {
        this.repository=repository;
        this.properties=properties;
    }
    private boolean owns(String ownerId, CurrentAccount actor) { return actor != null && ownerId.equals(actor.getId()); }
    private AppointmentResponse response(Appointment record) {
        AppointmentResponse result=new AppointmentResponse();
        result.setId(record.getId());
        result.setBuyerId(record.getBuyerId());
        result.setPropertyId(record.getPropertyId());
        result.setViewingDate(record.getViewingDate());
        result.setViewingTime(record.getViewingTime());
        result.setStatus(record.getStatus());
        result.setNotes(record.getNotes());
        return result;
    }
    private boolean readable(Appointment record, CurrentAccount actor) { return actor != null && (actor.isAdmin() || owns(record.getBuyerId(),actor) || (actor.isSeller() && properties.find(record.getPropertyId()).getOwnerId().equals(actor.getId()))); }
    @Override
    @Transactional(readOnly=true)
    //Detail
    public AppointmentResponse detail(String recordId, CurrentAccount actor) {
        Appointment record=repository.findById(id(recordId));
        allow(readable(record,actor));
        return response(record);
    }
    @Override
    @Transactional(readOnly=true)
    //Read
    public List<AppointmentResponse> list(CurrentAccount actor, String q, String city) {
        allow(actor != null);
        List<AppointmentResponse> result=new ArrayList<>();
        for (Appointment record : repository.findAll()) {
            if (!readable(record,actor)) continue;
            result.add(response(record));
        }
        return result;
    }
    //Creat
    @Override
    public AppointmentResponse create(CreateAppointmentRequest request, CurrentAccount actor) {
        allow(actor != null && actor.isBuyer());
        if (request==null) throw invalid("Request body is required.");
        Appointment record=new Appointment();
        record.setId(UUID.randomUUID().toString());
        record.setPropertyId(id(request.getPropertyId()));
        PropertyReference property=properties.lock(record.getPropertyId());
        conflict(!"AVAILABLE".equals(property.getStatus()),"Property is not available.");
        record.setBuyerId(actor.getId());
        record.setStatus("PENDING");
        fill(record,request);
        repository.insert(record);
        return response(record);
    }
    private void fill(Appointment record, CreateAppointmentRequest request) {
        if (request.getViewingDate()==null || request.getViewingTime()==null) throw invalid("Viewing date and time are required.");
        if (request.getViewingTime().getNano()!=0) throw invalid("Time must use whole seconds.");
        LocalDateTime scheduled=LocalDateTime.of(request.getViewingDate(),request.getViewingTime());
        if (!scheduled.isAfter(LocalDateTime.now(ZoneId.of("Asia/Colombo")))) throw invalid("Choose a future viewing time.");
        record.setViewingDate(request.getViewingDate());
        record.setViewingTime(request.getViewingTime());
        record.setNotes(optional(request.getNotes(),10000));
    }
    private void fill(Appointment record, UpdateAppointmentRequest request) {
        if (request.getViewingDate()==null || request.getViewingTime()==null) throw invalid("Viewing date and time are required.");
        if (request.getViewingTime().getNano()!=0) throw invalid("Time must use whole seconds.");
        LocalDateTime scheduled=LocalDateTime.of(request.getViewingDate(),request.getViewingTime());
        if (!scheduled.isAfter(LocalDateTime.now(ZoneId.of("Asia/Colombo")))) throw invalid("Choose a future viewing time.");
        record.setViewingDate(request.getViewingDate());
        record.setViewingTime(request.getViewingTime());
        record.setNotes(optional(request.getNotes(),10000));
    }
    @Override
    //Upadate
    public AppointmentResponse update(String recordId, UpdateAppointmentRequest request, CurrentAccount actor) {
        Appointment record=repository.lockById(id(recordId));
        allow(actor != null && actor.isBuyer() && owns(record.getBuyerId(),actor));
        if (request==null) throw invalid("Request body is required.");
        conflict(!"PENDING".equals(record.getStatus()),"Only pending records can be edited.");
        fill(record,request);
        repository.update(record);
        return response(record);
    }
    @Override
    //Delete
    public void delete(String recordId, CurrentAccount actor) {
        Appointment record=repository.lockById(id(recordId));
        allow(actor != null && (actor.isAdmin() || (actor.isBuyer() && owns(record.getBuyerId(),actor))));
        conflict(!"PENDING".equals(record.getStatus()),"Only pending records can be deleted.");
        repository.delete(record.getId());
    }
    @Override
    //Status Change
    public AppointmentResponse changeStatus(String recordId, String requested, CurrentAccount actor) {
        String status=choice(requested,"PENDING","CONFIRMED","REJECTED","CANCELLED","COMPLETED");
        // Lock the property first for all decisions to serialize slot checks.
        Appointment snapshot=repository.findById(id(recordId));
        PropertyReference property=properties.lock(snapshot.getPropertyId());
        Appointment record=repository.lockById(snapshot.getId());
        boolean buyer=actor != null && actor.isBuyer() && owns(record.getBuyerId(),actor);
        boolean seller=actor != null && actor.isSeller() && owns(property.getOwnerId(),actor);
        boolean admin=actor != null && actor.isAdmin();
        allow(buyer || seller || admin);
        if (status.equals(record.getStatus())) return response(record);
        boolean pending="PENDING".equals(record.getStatus());
        boolean confirmed="CONFIRMED".equals(record.getStatus());
        conflict(!pending && !confirmed,"Finished appointments cannot be reopened.");
        boolean allowed=false;
        if ("CANCELLED".equals(status) && (buyer || seller && confirmed || admin)) allowed=true;
        if (seller && pending && ("CONFIRMED".equals(status) || "REJECTED".equals(status))) allowed=true;
        if (seller && confirmed && "COMPLETED".equals(status)) allowed=true;
        allow(allowed);
        LocalDateTime scheduled=LocalDateTime.of(record.getViewingDate(),record.getViewingTime());
        LocalDateTime now=LocalDateTime.now(ZoneId.of("Asia/Colombo"));
        if ("CONFIRMED".equals(status)) {
            conflict(!scheduled.isAfter(now),"Cannot confirm a past viewing.");
            conflict(!"AVAILABLE".equals(property.getStatus()),"Property is not available.");
            conflict(repository.countConfirmedSlot(record.getPropertyId(),record.getViewingDate(),record.getViewingTime(),record.getId())>0,"That viewing slot is already confirmed.");
        }
        if ("COMPLETED".equals(status)) conflict(scheduled.isAfter(now),"A future viewing cannot be completed.");
        record.setStatus(status);
        repository.update(record);
        return response(record);
    }
}
