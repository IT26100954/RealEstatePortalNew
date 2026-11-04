package com.realestate.review;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.realestate.common.*;
import com.realestate.review.dto.*;
import static com.realestate.common.InputRules.*;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository repository;
    private final PropertyLookup properties;
    public ReviewServiceImpl(ReviewRepository repository, PropertyLookup properties) {
        this.repository=repository;
        this.properties=properties;
    }
    private boolean owns(String ownerId, CurrentAccount actor) { return actor != null && ownerId.equals(actor.getId()); }
    private ReviewResponse response(Review record) {
        ReviewResponse result=new ReviewResponse();
        result.setId(record.getId());
        result.setReviewerId(record.getReviewerId());
        result.setPropertyId(record.getPropertyId());
        result.setRating(record.getRating());
        result.setComment(record.getComment());
        return result;
    }
    private boolean readable(Review record, CurrentAccount actor) { return true; }
    @Override
    @Transactional(readOnly=true)
    public ReviewResponse detail(String recordId, CurrentAccount actor) {
        Review record=repository.findById(id(recordId));
        allow(readable(record,actor));
        return response(record);
    }
    @Override
    @Transactional(readOnly=true)
    public List<ReviewResponse> list(CurrentAccount actor, String q, String city) {
        List<ReviewResponse> result=new ArrayList<>();
        for (Review record : repository.findAll()) {
            if (!readable(record,actor)) continue;
            result.add(response(record));
        }
        return result;
    }
    @Override
    public ReviewResponse create(CreateReviewRequest request, CurrentAccount actor) {
        allow(actor != null && (actor.isBuyer() || actor.isSeller()));
        if (request==null) throw invalid("Request body is required.");
        Review record=new Review();
        record.setId(UUID.randomUUID().toString());
        record.setPropertyId(id(request.getPropertyId()));
        PropertyReference property=properties.lock(record.getPropertyId());
        allow(!property.getOwnerId().equals(actor.getId()));
        record.setReviewerId(actor.getId());
        fill(record,request);
        repository.insert(record);
        return response(record);
    }
    private void fill(Review record, CreateReviewRequest request) {
        if (request.getRating()==null) throw invalid("Rating is required.");
        int rating=request.getRating();
        if (rating<1 || rating>5) throw invalid("Rating must be from 1 to 5.");
        record.setRating(rating);
        record.setComment(text(request.getComment(),"Comment",10000));
    }
    private void fill(Review record, UpdateReviewRequest request) {
        if (request.getRating()==null) throw invalid("Rating is required.");
        int rating=request.getRating();
        if (rating<1 || rating>5) throw invalid("Rating must be from 1 to 5.");
        record.setRating(rating);
        record.setComment(text(request.getComment(),"Comment",10000));
    }
    @Override
    public ReviewResponse update(String recordId, UpdateReviewRequest request, CurrentAccount actor) {
        Review record=repository.lockById(id(recordId));
        allow(owns(record.getReviewerId(),actor));
        if (request==null) throw invalid("Request body is required.");
        fill(record,request);
        repository.update(record);
        return response(record);
    }
    @Override
    public void delete(String recordId, CurrentAccount actor) {
        Review record=repository.lockById(id(recordId));
        allow(actor != null && (actor.isAdmin() || owns(record.getReviewerId(),actor)));
        repository.delete(record.getId());
    }
}
