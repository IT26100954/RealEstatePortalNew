package com.realestate.property;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CurrentUser;
import com.realestate.property.dto.*;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyService service;
    private final CurrentUser currentUser;
    public PropertyController(PropertyService service, CurrentUser currentUser) {
        this.service=service;
        this.currentUser=currentUser;
    }
    @GetMapping
    public List<PropertyResponse> list(HttpSession session, @RequestParam(required=false) String q, @RequestParam(required=false) String city) {
        return service.list(currentUser.optional(session),q,city);
    }
    @GetMapping("/{id}")
    public PropertyResponse detail(@PathVariable String id, HttpSession session) {
        return service.detail(id,currentUser.optional(session));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(@RequestBody CreatePropertyRequest request, HttpSession session) {
        return service.create(request,currentUser.required(session));
    }
    @PutMapping("/{id}")
    public PropertyResponse update(@PathVariable String id,@RequestBody UpdatePropertyRequest request,HttpSession session) {
        return service.update(id,request,currentUser.required(session));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,HttpSession session) { service.delete(id,currentUser.required(session)); }
}
