package com.realestate.moving;

import java.util.List;
import com.realestate.common.CurrentUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/moving-companies")
public class MovingCompanyController {
    private final MovingCompanyService service;
    private final CurrentUser currentUser;

    public MovingCompanyController(MovingCompanyService service,CurrentUser currentUser) {
        this.service=service;
        this.currentUser=currentUser;
    }
    @GetMapping
    public List<MovingCompany> list(HttpSession session) {
        return service.list(currentUser.optional(session));
    }
    @GetMapping("/{id}")
    public MovingCompany detail(@PathVariable String id,HttpSession session) {
        return service.detail(id,currentUser.optional(session));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovingCompany create(@RequestBody MovingCompanyRequest request,HttpSession session) {
        return service.create(request,currentUser.required(session));
    }
    @PutMapping("/{id}")
    public MovingCompany update(@PathVariable String id,@RequestBody MovingCompanyRequest request,HttpSession session) {
        return service.update(id,request,currentUser.required(session));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,HttpSession session) {
        service.delete(id,currentUser.required(session));
    }
}
