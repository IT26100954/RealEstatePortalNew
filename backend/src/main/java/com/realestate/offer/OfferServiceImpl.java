package com.realestate.offer;

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
import com.realestate.offer.dto.*;
import static com.realestate.common.InputRules.*;

@Service
@Transactional
public class OfferServiceImpl implements OfferService {
    private final OfferRepository repository;
    private final PropertyLookup properties;
    public OfferServiceImpl(OfferRepository repository, PropertyLookup properties) {
        this.repository=repository;
        this.properties=properties;
    }
    private boolean owns(String ownerId, CurrentAccount actor) { return actor != null && ownerId.equals(actor.getId()); }
    private OfferResponse response(Offer record) {
        OfferResponse result=new OfferResponse();
        result.setId(record.getId());
        result.setBuyerId(record.getBuyerId());
        result.setPropertyId(record.getPropertyId());
        result.setAmount(record.getAmount().toPlainString());
        result.setMessage(record.getMessage());
        result.setStatus(record.getStatus());
        return result;
    }
    private boolean readable(Offer record, CurrentAccount actor) { return actor != null && (actor.isAdmin() || owns(record.getBuyerId(),actor) || (actor.isSeller() && properties.find(record.getPropertyId()).getOwnerId().equals(actor.getId()))); }
    @Override
    @Transactional(readOnly=true)
    public OfferResponse detail(String recordId, CurrentAccount actor) {
        Offer record=repository.findById(id(recordId));
        allow(readable(record,actor));
        return response(record);
    }
    @Override
    @Transactional(readOnly=true)
    public List<OfferResponse> list(CurrentAccount actor, String q, String city) {
        allow(actor != null);
        List<OfferResponse> result=new ArrayList<>();
        for (Offer record : repository.findAll()) {
            if (!readable(record,actor)) continue;
            result.add(response(record));
        }
        return result;
    }
    @Override
    public OfferResponse create(CreateOfferRequest request, CurrentAccount actor) {
        allow(actor != null && actor.isBuyer());
        if (request==null) throw invalid("Request body is required.");
        Offer record=new Offer();
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
    private void fill(Offer record, CreateOfferRequest request) {
        record.setAmount(money(request.getAmount()));
        record.setMessage(optional(request.getMessage(),10000));
    }
    private void fill(Offer record, UpdateOfferRequest request) {
        record.setAmount(money(request.getAmount()));
        record.setMessage(optional(request.getMessage(),10000));
    }
    @Override
    public OfferResponse update(String recordId, UpdateOfferRequest request, CurrentAccount actor) {
        Offer record=repository.lockById(id(recordId));
        allow(actor != null && actor.isBuyer() && owns(record.getBuyerId(),actor));
        if (request==null) throw invalid("Request body is required.");
        conflict(!"PENDING".equals(record.getStatus()),"Only pending records can be edited.");
        fill(record,request);
        repository.update(record);
        return response(record);
    }
    @Override
    public void delete(String recordId, CurrentAccount actor) {
        Offer record=repository.lockById(id(recordId));
        allow(actor != null && (actor.isAdmin() || (actor.isBuyer() && owns(record.getBuyerId(),actor))));
        conflict(!"PENDING".equals(record.getStatus()),"Only pending records can be deleted.");
        repository.delete(record.getId());
    }
    @Override
    public OfferResponse changeStatus(String recordId, String requested, CurrentAccount actor) {
        String status=choice(requested,"PENDING","ACCEPTED","REJECTED","WITHDRAWN");
        Offer record=repository.lockById(id(recordId));
        PropertyReference property=properties.find(record.getPropertyId());
        boolean buyer=actor != null && actor.isBuyer() && owns(record.getBuyerId(),actor);
        boolean seller=actor != null && actor.isSeller() && owns(property.getOwnerId(),actor);
        boolean admin=actor != null && actor.isAdmin();
        allow(buyer || seller || admin);
        if (status.equals(record.getStatus())) return response(record);
        conflict(!"PENDING".equals(record.getStatus()),"Finished offers cannot be reopened.");
        allow(buyer && "WITHDRAWN".equals(status) || seller && ("ACCEPTED".equals(status) || "REJECTED".equals(status)));
        record.setStatus(status);
        repository.update(record);
        return response(record);
    }
}
