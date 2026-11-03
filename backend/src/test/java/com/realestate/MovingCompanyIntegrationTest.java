package com.realestate;

import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
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
import org.springframework.web.bind.annotation.*;
import com.realestate.common.CsrfTokens;
import com.realestate.common.CurrentUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties={"spring.datasource.url=jdbc:h2:mem:moving_fix;MODE=MySQL;DB_CLOSE_DELAY=-1"})
@Import(MovingCompanyIntegrationTest.TestSessions.class)
class MovingCompanyIntegrationTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    static final String ADMIN="00000000-0000-0000-0000-000000000001";
    static final String BUYER="00000000-0000-0000-0000-000000000002";
    Client admin,buyer,guest;

    // Test-only session fixture, never part of production authentication.
    @RestController
    static class TestSessions {
        @GetMapping("/test/moving-session")
        public Map<String,String> session(@RequestParam String id,HttpSession session) {
            session.setAttribute(CurrentUser.SESSION_USER_ID,id);
            Map<String,String> response=new LinkedHashMap<>();
            response.put("csrfToken",CsrfTokens.getOrCreate(session));
            return response;
        }
    }
    class Client {
        final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        String token="";
        HttpResponse<String> send(String method,String path,String body) throws Exception {
            HttpRequest.Builder request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path)).header("Content-Type","application/json");
            if(!token.isEmpty())request.header("X-CSRF-TOKEN",token);
            request.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body));
            return client.send(request.build(),HttpResponse.BodyHandlers.ofString());
        }
    }
    static String value(String body,String key) {
        Matcher match=Pattern.compile("\""+key+"\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        if(!match.find())throw new AssertionError(body);return match.group(1);
    }
    String call(Client client,String method,String path,String body,int expected) throws Exception {
        HttpResponse<String> response=client.send(method,path,body);
        assertEquals(expected,response.statusCode(),response.body());return response.body();
    }
    Client login(String id) throws Exception {
        Client client=new Client();client.token=value(call(client,"GET","/test/moving-session?id="+id,null,200),"csrfToken");return client;
    }
    @BeforeEach void setup() throws Exception {
        jdbc.execute("CREATE TABLE IF NOT EXISTS users(id CHAR(36) PRIMARY KEY,username VARCHAR(40) UNIQUE NOT NULL,password_hash VARCHAR(255) NOT NULL,email VARCHAR(254) NOT NULL,phone VARCHAR(20) NOT NULL,role VARCHAR(20) NOT NULL)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS moving_companies(id CHAR(36) PRIMARY KEY,company_name VARCHAR(150) NOT NULL,email VARCHAR(254) NOT NULL,phone VARCHAR(20) NOT NULL,service_areas VARCHAR(500) NOT NULL,description TEXT NOT NULL,availability VARCHAR(20) NOT NULL,CHECK (availability IN ('AVAILABLE','UNAVAILABLE')))");
        for (String table:new String[]{"appointments","offers","reviews","moving_companies","properties","users"}) jdbc.update("DELETE FROM " + table);
        jdbc.update("INSERT INTO users (id,username,password_hash,email,phone,role) VALUES(?,?,?,?,?,?)",ADMIN,"test_admin","not-a-real-hash","admin@example.com","0771234567","ADMIN");
        jdbc.update("INSERT INTO users (id,username,password_hash,email,phone,role) VALUES(?,?,?,?,?,?)",BUYER,"test_buyer","not-a-real-hash","buyer@example.com","0771234567","BUYER");
        admin=login(ADMIN);buyer=login(BUYER);guest=new Client();
    }
    String body() {return "{\"companyName\":\"City Movers\",\"email\":\"move@example.com\",\"phone\":\"0771234567\",\"serviceAreas\":\"Colombo\",\"description\":\"Home relocation\",\"availability\":\"AVAILABLE\"}";}
    @Test void adminCrudAndPublicReads() throws Exception {
        String id=value(call(admin,"POST","/api/moving-companies",body(),201),"id");
        assertTrue(call(guest,"GET","/api/moving-companies",null,200).contains(id));
        call(guest,"GET","/api/moving-companies/"+id,null,200);
        String updated=call(admin,"PUT","/api/moving-companies/"+id,body().replace("AVAILABLE","UNAVAILABLE"),200);
        assertTrue(updated.contains("UNAVAILABLE"));
        assertTrue(call(guest,"GET","/api/moving-companies",null,200).contains("UNAVAILABLE"));
        call(admin,"DELETE","/api/moving-companies/"+id,null,204);
        call(guest,"GET","/api/moving-companies/"+id,null,404);
        assertEquals("[]",call(guest,"GET","/api/moving-companies",null,200));
    }
    @Test void validatesInputAndRejectsIdInjection() throws Exception {
        call(admin,"POST","/api/moving-companies",body().replace("City Movers"," "),400);
        call(admin,"POST","/api/moving-companies",body().replace("move@example.com","invalid"),400);
        call(admin,"POST","/api/moving-companies",body().replace("AVAILABLE","CLOSED"),400);
        call(admin,"POST","/api/moving-companies",body().replace("City Movers","x".repeat(151)),400);
        call(admin,"POST","/api/moving-companies",body().replace("\"companyName\"","\"id\":\"fake\",\"companyName\""),400);
        call(guest,"GET","/api/moving-companies/not-a-uuid",null,400);
    }
    @Test void checksRolesSessionsAndCsrf() throws Exception {
        call(guest,"POST","/api/moving-companies",body(),403);
        call(buyer,"POST","/api/moving-companies",body(),403);
        String id=value(call(admin,"POST","/api/moving-companies",body(),201),"id");
        call(buyer,"PUT","/api/moving-companies/"+id,body(),403);
        call(buyer,"DELETE","/api/moving-companies/"+id,null,403);
        jdbc.update("UPDATE users SET role='BUYER' WHERE id=?",ADMIN);
        call(admin,"DELETE","/api/moving-companies/"+id,null,403);
        jdbc.update("DELETE FROM users WHERE id=?",BUYER);
        call(buyer,"POST","/api/moving-companies",body(),401);
        admin.token="wrong";call(admin,"POST","/api/moving-companies",body(),403);
    }
}
