package com.realestate.appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.realestate.common.ModuleException;

@Repository
public class AppointmentRepositoryImpl implements AppointmentRepository {
    private final JdbcTemplate jdbc;
    public AppointmentRepositoryImpl(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    // Named mapper keeps the SQL-to-object conversion easy to follow.
    private static class Mapper implements RowMapper<Appointment> {
        @Override
        public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Appointment record=new Appointment();
            record.setId(rs.getString("id"));
            record.setBuyerId(rs.getString("buyer_id"));
            record.setPropertyId(rs.getString("property_id"));
            record.setViewingDate(rs.getDate("viewing_date").toLocalDate());
            record.setViewingTime(rs.getTime("viewing_time").toLocalTime());
            record.setStatus(rs.getString("status"));
            record.setNotes(rs.getString("notes"));
            return record;
        }
    }
    public List<Appointment> findAll() { return jdbc.query("SELECT id, buyer_id, property_id, viewing_date, viewing_time, status, notes FROM appointments ORDER BY id",new Mapper()); }
    public Appointment findById(String id) { return find(id,false); }
    public Appointment lockById(String id) { return find(id,true); }
    private Appointment find(String id, boolean locked) {
        String sql="SELECT id, buyer_id, property_id, viewing_date, viewing_time, status, notes FROM appointments WHERE id=?";
        if (locked) sql += " FOR UPDATE";
        List<Appointment> rows=jdbc.query(sql,new Mapper(),id);
        if (rows.isEmpty()) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
        return rows.get(0);
    }
    public void insert(Appointment record) {
        jdbc.update("INSERT INTO appointments (id, buyer_id, property_id, viewing_date, viewing_time, status, notes) VALUES (?,?,?,?,?,?,?)",
            record.getId(), record.getBuyerId(), record.getPropertyId(), java.sql.Date.valueOf(record.getViewingDate()), java.sql.Time.valueOf(record.getViewingTime()), record.getStatus(), record.getNotes());
    }
    public void update(Appointment record) {
        int changed=jdbc.update("UPDATE appointments SET buyer_id=?, property_id=?, viewing_date=?, viewing_time=?, status=?, notes=? WHERE id=?",
            record.getBuyerId(), record.getPropertyId(), java.sql.Date.valueOf(record.getViewingDate()), java.sql.Time.valueOf(record.getViewingTime()), record.getStatus(), record.getNotes(),record.getId());
        if (changed==0) findById(record.getId());
    }
    public void delete(String id) {
        int changed=jdbc.update("DELETE FROM appointments WHERE id=?",id);
        if (changed==0) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
    }
    public int countConfirmedSlot(String propertyId, java.time.LocalDate date, java.time.LocalTime time, String excludedId) {
        // Locking reads see the latest committed rows under MySQL's default
        // REPEATABLE READ, even after an earlier ordinary snapshot read.
        List<String> matches=jdbc.queryForList("SELECT id FROM appointments WHERE property_id=? AND viewing_date=? AND viewing_time=? AND status='CONFIRMED' AND id<>? FOR UPDATE",String.class,
            propertyId,java.sql.Date.valueOf(date),java.sql.Time.valueOf(time),excludedId);
        return matches.size();
    }
}
