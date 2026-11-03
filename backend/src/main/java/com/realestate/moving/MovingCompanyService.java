package com.realestate.moving;

import java.util.List;
import java.util.UUID;
import com.realestate.common.CurrentAccount;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.realestate.common.InputRules.*;

@Service
@Transactional
public class MovingCompanyService {
    private final MovingCompanyDAO dao;
    public MovingCompanyService(MovingCompanyDAO dao) { this.dao=dao; }

    @Transactional(readOnly=true)
    public List<MovingCompany> list(CurrentAccount actor) {
        return dao.all();
    }
    @Transactional(readOnly=true)
    public MovingCompany detail(String recordId,CurrentAccount actor) {
        return dao.find(id(recordId));
    }
    private void requireAdmin(CurrentAccount actor) {
        allow(actor != null && actor.isAdmin());
    }
    private MovingCompany prepare(String id,MovingCompanyRequest request) {
        if (request==null) throw invalid("Request body is required.");
        return new MovingCompany(id,
            text(request.getCompanyName(),"Company name",150),email(request.getEmail()),
            text(request.getPhone(),"Phone",20),text(request.getServiceAreas(),"Service areas",500),
            text(request.getDescription(),"Description",10000),
            choice(request.getAvailability(),"AVAILABLE","UNAVAILABLE"));
    }
    public MovingCompany create(MovingCompanyRequest request,CurrentAccount actor) {
        requireAdmin(actor);
        MovingCompany next=prepare(UUID.randomUUID().toString(),request);
        dao.insert(next);
        return next;
    }
    public MovingCompany update(String recordId,MovingCompanyRequest request,CurrentAccount actor) {
        requireAdmin(actor);
        String validId=id(recordId);
        dao.lock(validId);
        MovingCompany next=prepare(validId,request);
        dao.update(next);
        return next;
    }
    public void delete(String recordId,CurrentAccount actor) {
        requireAdmin(actor);
        String validId=id(recordId);
        dao.lock(validId);
        dao.delete(validId);
    }
}
