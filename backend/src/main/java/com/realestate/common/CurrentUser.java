package com.realestate.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public static final String SESSION_USER_ID = "userId";
    private final JdbcTemplate jdbc;
    public CurrentUser(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    // Member 1 stores this ID only after verifying login credentials.
    // Role is reloaded from MySQL rather than trusted from the client/session.
    public CurrentAccount optional(HttpSession session) {
        if (session == null) return null;
        Object value = session.getAttribute(SESSION_USER_ID);
        if (!(value instanceof String)) return null;
        List<CurrentAccount> rows = jdbc.query(
            "SELECT id, role FROM users WHERE id=?", new RowMapper<CurrentAccount>() {
                @Override
                public CurrentAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new CurrentAccount(rs.getString("id"), rs.getString("role"));
                }
            }, value);
        if (rows.isEmpty()) { session.invalidate(); return null; }
        return rows.get(0);
    }
    public CurrentAccount required(HttpSession session) {
        CurrentAccount account = optional(session);
        if (account == null) throw new ModuleException(HttpStatus.UNAUTHORIZED, "LOGIN_REQUIRED", "Please log in.");
        return account;
    }
}
