package com.realestate.property;

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
import com.realestate.property.dto.*;
import static com.realestate.common.InputRules.*;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository repository;
    private final PropertyLookup properties;
    public PropertyServiceImpl(PropertyRepository repository, PropertyLookup properties) {
        this.repository=repository;
        this.properties=properties;
    }
    private boolean owns(String ownerId, CurrentAccount actor) { return actor != null && ownerId.equals(actor.getId()); }
    private PropertyResponse response(Property record) {
        PropertyResponse result=new PropertyResponse();
        result.setId(record.getId());
        result.setOwnerId(record.getOwnerId());
        result.setTitle(record.getTitle());
        result.setDescription(record.getDescription());
        result.setCity(record.getCity());
        result.setAddress(record.getAddress());
        result.setPrice(record.getPrice().toPlainString());
        result.setPropertyType(record.getPropertyType());
        result.setStatus(record.getStatus());
        return result;
    }
    private boolean readable(Property record, CurrentAccount actor) { return "AVAILABLE".equals(record.getStatus()) || (actor != null && (actor.isAdmin() || owns(record.getOwnerId(),actor))); }
    @Override
    @Transactional(readOnly=true)
    public PropertyResponse detail(String recordId, CurrentAccount actor) {
        Property record=repository.findById(id(recordId));
        allow(readable(record,actor));
        return response(record);
    }
    @Override
    @Transactional(readOnly=true)
    public List<PropertyResponse> list(CurrentAccount actor, String q, String city) {
        List<PropertyResponse> result=new ArrayList<>();
        for (Property record : repository.findAll()) {
            if (!readable(record,actor)) continue;
            if (q != null && !q.trim().isEmpty()) {
                String term=q.trim().toLowerCase(Locale.ROOT);
                if (!record.getTitle().toLowerCase(Locale.ROOT).contains(term) && !record.getDescription().toLowerCase(Locale.ROOT).contains(term)) continue;
            }
            if (city != null && !city.trim().isEmpty() && !record.getCity().equalsIgnoreCase(city.trim())) continue;
            result.add(response(record));
        }
        return result;
    }
    @Override
    public PropertyResponse create(CreatePropertyRequest request, CurrentAccount actor) {
        allow(actor != null && actor.isSeller());
        if (request==null) throw invalid("Request body is required.");
        Property record=new Property();
        record.setId(UUID.randomUUID().toString());
        record.setOwnerId(actor.getId());
        fill(record,request);
        repository.insert(record);
        return response(record);
    }
    private void fill(Property record, CreatePropertyRequest request) {
        record.setTitle(text(request.getTitle(),"Title",150));
        record.setDescription(text(request.getDescription(),"Description",10000));
        record.setCity(text(request.getCity(),"City",100));
        record.setAddress(text(request.getAddress(),"Address",255));
        record.setPrice(money(request.getPrice()));
        record.setPropertyType(choice(request.getPropertyType(),"HOUSE","APARTMENT","LAND"));
        record.setStatus(choice(request.getStatus(),"AVAILABLE","UNAVAILABLE"));
    }
    private void fill(Property record, UpdatePropertyRequest request) {
        record.setTitle(text(request.getTitle(),"Title",150));
        record.setDescription(text(request.getDescription(),"Description",10000));
        record.setCity(text(request.getCity(),"City",100));
        record.setAddress(text(request.getAddress(),"Address",255));
        record.setPrice(money(request.getPrice()));
        record.setPropertyType(choice(request.getPropertyType(),"HOUSE","APARTMENT","LAND"));
        record.setStatus(choice(request.getStatus(),"AVAILABLE","UNAVAILABLE"));
    }
    @Override
    public PropertyResponse update(String recordId, UpdatePropertyRequest request, CurrentAccount actor) {
        Property record=repository.lockById(id(recordId));
        allow(actor != null && (actor.isAdmin() || (actor.isSeller() && owns(record.getOwnerId(),actor))));
        if (request==null) throw invalid("Request body is required.");
        fill(record,request);
        repository.update(record);
        return response(record);
    }
    @Override
    public void delete(String recordId, CurrentAccount actor) {
        Property record=repository.lockById(id(recordId));
        allow(actor != null && (actor.isAdmin() || (actor.isSeller() && owns(record.getOwnerId(),actor))));
        repository.delete(record.getId());
    }
}
