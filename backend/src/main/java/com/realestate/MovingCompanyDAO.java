package com.realestate.moving;

import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Repository
public class MovingCompanyDAO {
    private final JdbcTemplate jdbc;

    private final RowMapper<MovingCompany> mapper = (rs, n) -> new MovingCompany(
            rs.getString("id"),
            rs.getString("company_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("service_areas"),
            rs.getString("description"),
            rs.getString("availability"));

    public MovingCompanyDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MovingCompany> all() {
        return jdbc.query("SELECT * FROM moving_companies ORDER BY id", mapper);
    }

    public MovingCompany find(String id) {
        return jdbc.query("SELECT * FROM moving_companies WHERE id=?", mapper, id).stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    }

    public void insert(MovingCompany e) {
        jdbc.update("INSERT INTO moving_companies(id,company_name,email,phone,service_areas,description,availability)"
                        + " VALUES(?,?,?,?,?,?,?)",
                e.id(), e.companyName(), e.email(), e.phone(), e.serviceAreas(), e.description(), e.availability());
    }

    public void update(MovingCompany e) {
        jdbc.update("UPDATE moving_companies SET company_name=?,email=?,phone=?,service_areas=?,description=?,availability=? WHERE id=?",
                e.companyName(), e.email(), e.phone(), e.serviceAreas(), e.description(), e.availability(), e.id());
    }

    public void delete(String id) {
        jdbc.update("DELETE FROM moving_companies WHERE id=?", id);
    }
}
