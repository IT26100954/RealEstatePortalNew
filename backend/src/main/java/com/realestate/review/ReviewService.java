package com.realestate.review;

import java.util.List;
import com.realestate.common.CurrentAccount;
import com.realestate.review.dto.*;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request, CurrentAccount actor);
    ReviewResponse detail(String id, CurrentAccount actor);
    List<ReviewResponse> list(CurrentAccount actor, String q, String city);
    ReviewResponse update(String id, UpdateReviewRequest request, CurrentAccount actor);
    void delete(String id, CurrentAccount actor);
}
