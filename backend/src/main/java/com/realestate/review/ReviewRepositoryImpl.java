package com.realestate.review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.realestate.common.ModuleException;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {
    private final JdbcTemplate jdbc;
    public ReviewRepositoryImpl(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    // Named mapper keeps the SQL-to-object conversion easy to follow.
    private static class Mapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review record=new Review();
            record.setId(rs.getString("id"));
            record.setReviewerId(rs.getString("reviewer_id"));
            record.setPropertyId(rs.getString("property_id"));
            record.setRating(rs.getInt("rating"));
            record.setComment(rs.getString("comment"));
            return record;
        }
    }
    public List<Review> findAll() { return jdbc.query("SELECT id, reviewer_id, property_id, rating, comment FROM reviews ORDER BY id",new Mapper()); }
    public Review findById(String id) { return find(id,false); }
    public Review lockById(String id) { return find(id,true); }
    private Review find(String id, boolean locked) {
        String sql="SELECT id, reviewer_id, property_id, rating, comment FROM reviews WHERE id=?";
        if (locked) sql += " FOR UPDATE";
        List<Review> rows=jdbc.query(sql,new Mapper(),id);
        if (rows.isEmpty()) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
        return rows.get(0);
    }
    public void insert(Review record) {
        jdbc.update("INSERT INTO reviews (id, reviewer_id, property_id, rating, comment) VALUES (?,?,?,?,?)",
            record.getId(), record.getReviewerId(), record.getPropertyId(), record.getRating(), record.getComment());
    }
    public void update(Review record) {
        int changed=jdbc.update("UPDATE reviews SET reviewer_id=?, property_id=?, rating=?, comment=? WHERE id=?",
            record.getReviewerId(), record.getPropertyId(), record.getRating(), record.getComment(),record.getId());
        if (changed==0) findById(record.getId());
    }
    public void delete(String id) {
        int changed=jdbc.update("DELETE FROM reviews WHERE id=?",id);
        if (changed==0) throw new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Record not found.");
    }
}
