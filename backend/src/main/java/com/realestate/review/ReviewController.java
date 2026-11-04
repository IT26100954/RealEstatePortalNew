package com.realestate.review;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CurrentUser;
import com.realestate.review.dto.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService service;
    private final CurrentUser currentUser;
    public ReviewController(ReviewService service, CurrentUser currentUser) {
        this.service=service;
        this.currentUser=currentUser;
    }
    @GetMapping
    public List<ReviewResponse> list(HttpSession session, @RequestParam(required=false) String q, @RequestParam(required=false) String city) {
        return service.list(currentUser.optional(session),q,city);
    }
    @GetMapping("/{id}")
    public ReviewResponse detail(@PathVariable String id, HttpSession session) {
        return service.detail(id,currentUser.optional(session));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@RequestBody CreateReviewRequest request, HttpSession session) {
        return service.create(request,currentUser.required(session));
    }
    @PutMapping("/{id}")
    public ReviewResponse update(@PathVariable String id,@RequestBody UpdateReviewRequest request,HttpSession session) {
        return service.update(id,request,currentUser.required(session));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,HttpSession session) { service.delete(id,currentUser.required(session)); }
}
