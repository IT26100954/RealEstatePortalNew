package com.realestate.offer;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CurrentUser;
import com.realestate.offer.dto.*;

@RestController
@RequestMapping("/api/offers")
public class OfferController {
    private final OfferService service;
    private final CurrentUser currentUser;
    public OfferController(OfferService service, CurrentUser currentUser) {
        this.service=service;
        this.currentUser=currentUser;
    }
    @GetMapping
    public List<OfferResponse> list(HttpSession session, @RequestParam(required=false) String q, @RequestParam(required=false) String city) {
        return service.list(currentUser.required(session),q,city);
    }
    @GetMapping("/{id}")
    public OfferResponse detail(@PathVariable String id, HttpSession session) {
        return service.detail(id,currentUser.required(session));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferResponse create(@RequestBody CreateOfferRequest request, HttpSession session) {
        return service.create(request,currentUser.required(session));
    }
    @PutMapping("/{id}")
    public OfferResponse update(@PathVariable String id,@RequestBody UpdateOfferRequest request,HttpSession session) {
        return service.update(id,request,currentUser.required(session));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,HttpSession session) { service.delete(id,currentUser.required(session)); }
    @PatchMapping("/{id}/status")
    public OfferResponse status(@PathVariable String id,@RequestBody OfferStatusRequest request,HttpSession session) {
        return service.changeStatus(id,request.getStatus(),currentUser.required(session));
    }
}
