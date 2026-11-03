package com.realestate;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.realestate.common.CsrfTokens;
import com.realestate.common.CurrentUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AppointmentIntegrationTest.TestSessions.class)
class AppointmentIntegrationTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    static final String BUYER="00000000-0000-0000-0000-000000000001";
    static final String SELLER="00000000-0000-0000-0000-000000000002";
    static final String ADMIN="00000000-0000-0000-0000-000000000003";
    static final String OTHER="00000000-0000-0000-0000-000000000004";
    static final String PROPERTY="00000000-0000-0000-0000-000000000005";
    Client buyer,seller,admin,other,guest;
    String date;

    // Deliberately test-only. NEVER copy this endpoint into src/main.
    @RestController
    static class TestSessions {
        @GetMapping("/test/session")
        public Map<String,String> session(@RequestParam String id,HttpSession session) {
            session.setAttribute(CurrentUser.SESSION_USER_ID,id);
            Map<String,String> result=new LinkedHashMap<>();
            result.put("csrfToken",CsrfTokens.getOrCreate(session));
            return result;
        }
    }
    class Client {
        final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        String token="";
        HttpResponse<String> send(String method,String path,String body) throws Exception {
            HttpRequest.Builder request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path));
            request.header("Content-Type","application/json");
            if (!token.isEmpty()) request.header("X-CSRF-TOKEN",token);
            request.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body));
            return client.send(request.build(),HttpResponse.BodyHandlers.ofString());
        }
    }
    Client login(String id) throws Exception {
        Client c=new Client();c.token=value(c.send("GET","/test/session?id="+id,null).body(),"csrfToken");return c;
    }
    static String value(String json,String key) {
        Matcher matcher=Pattern.compile("\""+key+"\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        if(!matcher.find()) throw new AssertionError("Missing "+key+" in "+json);
        return matcher.group(1);
    }
    String call(Client c,String method,String path,String body,int status) throws Exception {
        HttpResponse<String> response=c.send(method,path,body);
        assertEquals(status,response.statusCode(),response.body());return response.body();
    }
    @BeforeEach void setup() throws Exception {
        for(String table:new String[]{"appointments","offers","reviews","moving_companies","properties","users"})jdbc.update("DELETE FROM "+table);
        addUser(BUYER,"buyer","BUYER");addUser(SELLER,"seller","SELLER");addUser(ADMIN,"admin","ADMIN");addUser(OTHER,"other","SELLER");
        jdbc.update("INSERT INTO properties VALUES(?,?,?,?,?,?,?,?,?)",PROPERTY,SELLER,"Garden home","Quiet home","Colombo","Green Road",new java.math.BigDecimal("1000000.00"),"HOUSE","AVAILABLE");
        buyer=login(BUYER);seller=login(SELLER);admin=login(ADMIN);other=login(OTHER);guest=new Client();date=LocalDate.now(ZoneId.of("Asia/Colombo")).plusDays(3).toString();
    }
    void addUser(String id,String name,String role) {jdbc.update("INSERT INTO users VALUES(?,?,?,?,?,?)",id,name,"not-a-real-password-hash",name+"@example.com","0771234567",role);}
    String propertyBody(String status){return "{\"title\":\"New home\",\"description\":\"Family home\",\"city\":\"Kandy\",\"address\":\"Lake Road\",\"price\":\"2500000.00\",\"propertyType\":\"HOUSE\",\"status\":\""+status+"\"}";}
    String appointmentBody(){return "{\"propertyId\":\""+PROPERTY+"\",\"viewingDate\":\""+date+"\",\"viewingTime\":\"10:00:00\",\"notes\":null}";}
    String offerBody(String amount){return "{\"propertyId\":\""+PROPERTY+"\",\"amount\":\""+amount+"\",\"message\":\"Offer\"}";}

    @Test void appointmentCrudAndStatusPolicy() throws Exception {
        call(seller,"POST","/api/appointments",appointmentBody(),403);
        String id=value(call(buyer,"POST","/api/appointments",appointmentBody(),201),"id");
        call(buyer,"POST","/api/appointments",appointmentBody(),201); // pending duplicates allowed
        call(other,"GET","/api/appointments/"+id,null,403);
        assertFalse(call(other,"GET","/api/appointments",null,200).contains(id));
        call(seller,"GET","/api/appointments/"+id,null,200);
        String update="{\"viewingDate\":\""+date+"\",\"viewingTime\":\"11:00:00\",\"notes\":\"New time\"}";
        call(buyer,"PUT","/api/appointments/"+id,update,200);
        call(admin,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CONFIRMED\"}",403);
        call(seller,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CONFIRMED\"}",200);
        call(buyer,"DELETE","/api/appointments/"+id,null,409);
        call(seller,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"COMPLETED\"}",409);
        call(buyer,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CANCELLED\"}",200);
        call(buyer,"PUT","/api/appointments/"+id,update,409);
        String pending=value(call(buyer,"POST","/api/appointments",appointmentBody(),201),"id");
        call(buyer,"DELETE","/api/appointments/"+pending,null,204);
    }
    @Test void concurrentAppointmentConfirmationsSerialize() throws Exception {
        final String a=value(call(buyer,"POST","/api/appointments",appointmentBody(),201),"id");
        final String b=value(call(buyer,"POST","/api/appointments",appointmentBody(),201),"id");
        ExecutorService pool=Executors.newFixedThreadPool(2);
        try {
            Future<Integer> first=pool.submit(new Callable<Integer>() {public Integer call() throws Exception {return seller.send("PATCH","/api/appointments/"+a+"/status","{\"status\":\"CONFIRMED\"}").statusCode();}});
            Future<Integer> second=pool.submit(new Callable<Integer>() {public Integer call() throws Exception {return seller.send("PATCH","/api/appointments/"+b+"/status","{\"status\":\"CONFIRMED\"}").statusCode();}});
            int x=first.get();int y=second.get();assertTrue(x==200 && y==409 || x==409 && y==200,"Results: "+x+", "+y);
            assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM appointments WHERE status='CONFIRMED'",Integer.class));
        } finally {pool.shutdownNow();}
    }

    @Test void rejectsInvalidOrSpoofedAppointmentInput() throws Exception {
        call(guest,"GET","/api/appointments",null,401);
        call(guest,"POST","/api/appointments",appointmentBody(),403);
        call(buyer,"POST","/api/appointments",appointmentBody().replace(date,"2020-01-01"),400);
        call(buyer,"POST","/api/appointments",appointmentBody().replace("\"notes\"","\"buyerId\":\"fake\",\"notes\""),400);
        call(buyer,"POST","/api/appointments",appointmentBody().replace("10:00:00","10:00:00.123"),400);
        call(buyer,"POST","/api/appointments",appointmentBody().replace(PROPERTY,"11111111-1111-1111-1111-111111111111"),404);
        jdbc.update("UPDATE properties SET status='UNAVAILABLE' WHERE id=?",PROPERTY);
        call(buyer,"POST","/api/appointments",appointmentBody(),409);
    }
    @Test void confirmedAndCompletedRulesAreEnforced() throws Exception {
        String id=value(call(buyer,"POST","/api/appointments",appointmentBody(),201),"id");
        call(other,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CONFIRMED\"}",403);
        call(seller,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CONFIRMED\"}",200);
        call(seller,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CONFIRMED\"}",200);
        jdbc.update("UPDATE appointments SET viewing_date=?,viewing_time=? WHERE id=?",java.sql.Date.valueOf("2020-01-01"),java.sql.Time.valueOf("10:00:00"),id);
        call(seller,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"COMPLETED\"}",200);
        call(seller,"PATCH","/api/appointments/"+id+"/status","{\"status\":\"CONFIRMED\"}",409);
        call(admin,"DELETE","/api/appointments/"+id,null,409);
        call(buyer,"PUT","/api/appointments/"+id,"{\"viewingDate\":\""+date+"\",\"viewingTime\":\"11:00:00\",\"notes\":null}",409);
    }
    @Test void pendingDeleteAndLiveRoleChecks() throws Exception {
        String id=value(call(buyer,"POST","/api/appointments",appointmentBody(),201),"id");
        call(seller,"DELETE","/api/appointments/"+id,null,403);
        call(admin,"DELETE","/api/appointments/"+id,null,204);
        call(buyer,"GET","/api/appointments/"+id,null,404);
        jdbc.update("UPDATE users SET role='SELLER' WHERE id=?",BUYER);
        call(buyer,"POST","/api/appointments",appointmentBody(),403);
    }

}
