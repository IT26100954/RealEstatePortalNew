package com.realestate.offer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.realestate.common.ModuleException;

@Repository
public class OfferRepositoryImpl implements OfferRepository {
    private final JdbcTemplate jdbc;
    public OfferRepositoryImpl(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    // Named mapper keeps the SQL-to-object conversion easy to follow.
    private static class Mapper implements RowMapper<Offer> {
        @Override
        public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Offer record=new Offer();
            record.setId(rs.getString("id"));
            record.setBuyerId(rs.getString("buyer_id"));
            record.setPropertyId(rs.getString("property_id"));
            record.setAmount(rs.getBigDecimal("amount"));
            record.setMessage(rs.getString("message"));
            record.setStatus(rs.getString("status"));
            return record;
        }
    }
    public List<Offer> findAll() { return jdbc.query("SELECT id, buyer_id, property_id, amount, message, status FROM offers ORDER BY id",new Mapper()); }
    public Offer findById(String id) { return find(id,false); }
    public Offer lockById(String id) { return find(id,true); }
    private Offer find(String id, boolean locked) {
        String sql="SELECT id, buyer_id, property_id, amount, message, status FROM offers WHERE id=?";
        if (locked) sql += " FOR UPDATE";
        List<Offer> rows=jdbc.query(sql,new Mapper(),id);
        if (rows.isEmpty()) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
        return rows.get(0);
    }
    public void insert(Offer record) {
        jdbc.update("INSERT INTO offers (id, buyer_id, property_id, amount, message, status) VALUES (?,?,?,?,?,?)",
            record.getId(), record.getBuyerId(), record.getPropertyId(), record.getAmount(), record.getMessage(), record.getStatus());
    }
    public void update(Offer record) {
        int changed=jdbc.update("UPDATE offers SET buyer_id=?, property_id=?, amount=?, message=?, status=? WHERE id=?",
            record.getBuyerId(), record.getPropertyId(), record.getAmount(), record.getMessage(), record.getStatus(),record.getId());
        if (changed==0) findById(record.getId());
    }
    public void delete(String id) {
        int changed=jdbc.update("DELETE FROM offers WHERE id=?",id);
        if (changed==0) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
    }
}
