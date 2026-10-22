package com.realestate.moving;

import com.realestate.common.Access;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/moving-companies")
public class MovingCompanyController {
    private final MovingCompanyService service;
    private final Access access;

    //Paramaterized Constructor
    public MovingCompanyController(MovingCompanyService service, Access access) {
        this.service = service;
        this.access = access;
    }

    @GetMapping
    public Object list(HttpSession s) {
        return service.list(access.current(s));
    }

    @GetMapping("/{id}")
    public Object detail(@PathVariable String id, HttpSession s) {
        return service.detail(id, access.current(s));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@RequestBody MovingCompany d, HttpSession s) {
        return service.create(d, access.required(s));
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody MovingCompany d, HttpSession s) {
        return service.update(id, d, access.required(s));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, HttpSession s) {
        service.delete(id, access.required(s));
    }
}
