package com.realestate.moving;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.http.HttpStatus;
import com.realestate.common.ModuleException;

@Repository
public class MovingCompanyDAO {
    private final JdbcTemplate jdbc;
    private static class CompanyMapper implements RowMapper<MovingCompany> {
        @Override
        public MovingCompany mapRow(ResultSet rs,int rowNum) throws SQLException {
            return new MovingCompany(rs.getString("id"),rs.getString("company_name"),
                rs.getString("email"),rs.getString("phone"),rs.getString("service_areas"),
                rs.getString("description"),rs.getString("availability"));
        }
    }
    public MovingCompanyDAO(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public List<MovingCompany> all() {
        return jdbc.query("SELECT * FROM moving_companies ORDER BY id",new CompanyMapper());
    }
    public MovingCompany find(String id) { return read(id,false); }
    public MovingCompany lock(String id) { return read(id,true); }
    private MovingCompany read(String id,boolean locked) {
        String sql="SELECT * FROM moving_companies WHERE id=?";
        if (locked) sql += " FOR UPDATE";
        List<MovingCompany> rows=jdbc.query(sql,new CompanyMapper(),id);
        if (rows.isEmpty()) throw missing();
        return rows.get(0);
    }
    private ModuleException missing() {
        return new ModuleException(HttpStatus.NOT_FOUND,"RECORD_NOT_FOUND","Moving company not found.");
    }
    public void insert(MovingCompany record) {
        jdbc.update("INSERT INTO moving_companies(id,company_name,email,phone,service_areas,description,availability) VALUES(?,?,?,?,?,?,?)",
            record.id(),record.companyName(),record.email(),record.phone(),record.serviceAreas(),record.description(),record.availability());
    }
    public void update(MovingCompany record) {
        int changed=jdbc.update("UPDATE moving_companies SET company_name=?,email=?,phone=?,service_areas=?,description=?,availability=? WHERE id=?",
            record.companyName(),record.email(),record.phone(),record.serviceAreas(),record.description(),record.availability(),record.id());
        if (changed==0) find(record.id());
    }
    public void delete(String id) {
        if (jdbc.update("DELETE FROM moving_companies WHERE id=?",id)==0) throw missing();
    }
}
