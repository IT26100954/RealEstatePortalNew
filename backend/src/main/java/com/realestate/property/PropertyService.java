package com.realestate.property;

import java.util.List;
import com.realestate.common.CurrentAccount;
import com.realestate.property.dto.*;

public interface PropertyService {
    PropertyResponse create(CreatePropertyRequest request, CurrentAccount actor);
    PropertyResponse detail(String id, CurrentAccount actor);
    List<PropertyResponse> list(CurrentAccount actor, String q, String city);
    PropertyResponse update(String id, UpdatePropertyRequest request, CurrentAccount actor);
    void delete(String id, CurrentAccount actor);
}
