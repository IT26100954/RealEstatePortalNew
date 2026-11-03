package com.realestate.property;

import java.util.List;

public interface PropertyRepository {
    List<Property> findAll();
    Property findById(String id);
    Property lockById(String id);
    void insert(Property record);
    void update(Property record);
    void delete(String id);
}
