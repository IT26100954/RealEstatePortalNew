package com.realestate.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

// Shared read/lock adapter owned with the property module; it never edits data.
@Component
public class PropertyLookup {
    private final JdbcTemplate jdbc;
    public PropertyLookup(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public PropertyReference find(String id) { return query(id, false); }
    public PropertyReference lock(String id) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) throw new IllegalStateException("Property lock requires a transaction.");
        return query(id, true);
    }
    private PropertyReference query(String id, boolean locked) {
        String sql="SELECT id, owner_id, status FROM properties WHERE id=?";
        if (locked) sql += " FOR UPDATE";
        List<PropertyReference> rows=jdbc.query(sql, new RowMapper<PropertyReference>() {
            @Override
            public PropertyReference mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new PropertyReference(rs.getString("id"),rs.getString("owner_id"),rs.getString("status"));
            }
        }, id);
        if (rows.isEmpty()) throw new ModuleException(HttpStatus.NOT_FOUND,"PROPERTY_NOT_FOUND","Property not found.");
        return rows.get(0);
    }
}
