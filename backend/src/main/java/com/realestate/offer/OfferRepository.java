package com.realestate.offer;

import java.util.List;

public interface OfferRepository {
    List<Offer> findAll();
    Offer findById(String id);
    Offer lockById(String id);
    void insert(Offer record);
    void update(Offer record);
    void delete(String id);
}
