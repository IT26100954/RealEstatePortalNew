package com.realestate.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.realestate.common.ModuleException;

@Repository
public class PropertyRepositoryImpl implements PropertyRepository {
    private final JdbcTemplate jdbc;
    public PropertyRepositoryImpl(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    // Named mapper keeps the SQL-to-object conversion easy to follow.
    private static class Mapper implements RowMapper<Property> {
        @Override
        public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
            Property record=new Property();
            record.setId(rs.getString("id"));
            record.setOwnerId(rs.getString("owner_id"));
            record.setTitle(rs.getString("title"));
            record.setDescription(rs.getString("description"));
            record.setCity(rs.getString("city"));
            record.setAddress(rs.getString("address"));
            record.setPrice(rs.getBigDecimal("price"));
            record.setPropertyType(rs.getString("property_type"));
            record.setStatus(rs.getString("status"));
            return record;
        }
    }
    public List<Property> findAll() { return jdbc.query("SELECT id, owner_id, title, description, city, address, price, property_type, status FROM properties ORDER BY id",new Mapper()); }
    public Property findById(String id) { return find(id,false); }
    public Property lockById(String id) { return find(id,true); }
    private Property find(String id, boolean locked) {
        String sql="SELECT id, owner_id, title, description, city, address, price, property_type, status FROM properties WHERE id=?";
        if (locked) sql += " FOR UPDATE";
        List<Property> rows=jdbc.query(sql,new Mapper(),id);
        if (rows.isEmpty()) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
        return rows.get(0);
    }
    public void insert(Property record) {
        jdbc.update("INSERT INTO properties (id, owner_id, title, description, city, address, price, property_type, status) VALUES (?,?,?,?,?,?,?,?,?)",
            record.getId(), record.getOwnerId(), record.getTitle(), record.getDescription(), record.getCity(), record.getAddress(), record.getPrice(), record.getPropertyType(), record.getStatus());
    }
    public void update(Property record) {
        int changed=jdbc.update("UPDATE properties SET owner_id=?, title=?, description=?, city=?, address=?, price=?, property_type=?, status=? WHERE id=?",
            record.getOwnerId(), record.getTitle(), record.getDescription(), record.getCity(), record.getAddress(), record.getPrice(), record.getPropertyType(), record.getStatus(),record.getId());
        if (changed==0) findById(record.getId());
    }
    public void delete(String id) {
        int changed=jdbc.update("DELETE FROM properties WHERE id=?",id);
        if (changed==0) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
    }
}
