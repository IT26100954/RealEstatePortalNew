package com.realestate.review;

import java.util.List;

public interface ReviewRepository {
    List<Review> findAll();
    Review findById(String id);
    Review lockById(String id);
    void insert(Review record);
    void update(Review record);
    void delete(String id);
}
