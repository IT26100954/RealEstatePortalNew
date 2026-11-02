package com.realestate.moving;

import com.realestate.common.*;
import com.realestate.user.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.time.*;

import static com.realestate.common.Rules.*;

@Service
@Transactional
public class MovingCompanyService {
    private final MovingCompanyDAO dao;
    private final References refs;
    private final JdbcTemplate jdbc;

    public MovingCompanyService(MovingCompanyDAO dao, References refs, JdbcTemplate jdbc) {
        this.dao = dao;
        this.refs = refs;
        this.jdbc = jdbc;
    }

    public List<MovingCompany> list(Actor a) {
        return dao.all().stream().filter(e -> readable(e, a)).toList();
    }

    public MovingCompany detail(String id, Actor a) {
        var e = dao.find(id);
        allow(readable(e, a));
        return e;
    }

    public boolean readable(MovingCompany e, Actor a) {
        return true;
    }

    public boolean editable(MovingCompany e, Actor a) {
        return a != null && a.admin();
    }

    private MovingCompany prepare(String id, MovingCompany d, Actor a) {
        allow(a != null && a.admin());
        return new MovingCompany(id, text(d.companyName(), "Company name", 150), email(d.email()), phone(d.phone()), text(d.serviceAreas(), "Service areas", 500), text(d.description(), "Description", 10000), choice(d.availability(), "AVAILABLE", "UNAVAILABLE"));
    }

    public MovingCompany create(MovingCompany d, Actor a) {
        var next = prepare(UUID.randomUUID().toString(), d, a);
        dao.insert(next);
        return next;
    }

    public MovingCompany update(String id, MovingCompany d, Actor a) {
        dao.find(id);
        var next = prepare(id, d, a);
        dao.update(next);
        return next;
    }

    public void delete(String id, Actor a) {
        allow(editable(dao.find(id), a));
        dao.delete(id);
    }

}
