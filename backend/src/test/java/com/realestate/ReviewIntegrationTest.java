package com.realestate;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.realestate.user.BootstrapAdminService;
import com.realestate.common.ModuleException;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewIntegrationTest {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;
    @Autowired BootstrapAdminService bootstrap;
    static final String ADMIN="10000000-0000-0000-0000-000000000001";
    static final String BUYER="10000000-0000-0000-0000-000000000002";
    static final String SELLER="10000000-0000-0000-0000-000000000003";
    static final String PROPERTY="10000000-0000-0000-0000-000000000004";
    static final String PASSWORD="ExamplePass123!";
    String seedHash;
    @BeforeEach void reset() {
        for(String table:new String[]{"appointments","offers","reviews","moving_companies","properties","users"})jdbc.update("DELETE FROM "+table);
        seedHash=encoder.encode(PASSWORD);
        seed(ADMIN,"admin","ADMIN");seed(BUYER,"buyer","BUYER");seed(SELLER,"seller","SELLER");
    }
    void seed(String id,String username,String role){jdbc.update("INSERT INTO users (id,username,password_hash,email,phone,role) VALUES(?,?,?,?,?,?)",id,username,seedHash,username+"@example.com","0771234567",role);}
    class Client {
        HttpClient http=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).build();
        String token="";
        HttpResponse<String> send(String method,String path,String json) throws Exception {
            HttpRequest.Builder req=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path)).header("Content-Type","application/json");
            if(!token.isEmpty())req.header("X-CSRF-TOKEN",token);
            req.method(method,json==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(json));
            return http.send(req.build(),HttpResponse.BodyHandlers.ofString());
        }
        void refresh() throws Exception {token=value(call(this,"GET","/api/auth/me",null,200),"csrfToken");}
        void login(String username) throws Exception {
            refresh();String body=call(this,"POST","/api/auth/login","{\"username\":\""+username+"\",\"password\":\""+PASSWORD+"\"}",200);
            token=value(body,"csrfToken");
        }
    }
    String call(Client client,String method,String path,String json,int status) throws Exception {
        HttpResponse<String> response=client.send(method,path,json);assertEquals(status,response.statusCode(),response.body());return response.body();
    }
    static String value(String json,String key){Matcher m=Pattern.compile("\""+key+"\"\\s*:\\s*\"([^\"]+)\"").matcher(json);if(!m.find())throw new AssertionError(json);return m.group(1);}
    String create(String name,String role){return "{\"username\":\""+name+"\",\"password\":\""+PASSWORD+"\",\"email\":\""+name+"@example.com\",\"phone\":\"0771234567\",\"role\":\""+role+"\"}";}
    String profile(String name){return "{\"username\":\""+name+"\",\"email\":\"changed@example.com\",\"phone\":\"0711234567\"}";}


    void property() {jdbc.update("INSERT INTO properties(id,owner_id,title,description,city,address,price,property_type,status) VALUES(?,?,?,?,?,?,?,?,?)",PROPERTY,SELLER,"Review home","Test","Kandy","Road",100,"HOUSE","AVAILABLE");}
    String review() {return "{\"propertyId\":\""+PROPERTY+"\",\"rating\":4,\"comment\":\"Good home\"}";}
    @Test void reviewCrudAndPermissions() throws Exception {
      property();Client buyer=new Client();buyer.login("buyer");Client seller=new Client();seller.login("seller");Client admin=new Client();admin.login("admin");Client guest=new Client();guest.refresh();
      call(guest,"POST","/api/reviews",review(),401);call(seller,"POST","/api/reviews",review(),403);call(admin,"POST","/api/reviews",review(),403);
      String id=value(call(buyer,"POST","/api/reviews",review(),201),"id");
      assertTrue(call(guest,"GET","/api/reviews",null,200).contains(id));call(guest,"GET","/api/reviews/"+id,null,200);
      call(admin,"PUT","/api/reviews/"+id,"{\"rating\":2,\"comment\":\"Changed\"}",403);call(seller,"DELETE","/api/reviews/"+id,null,403);
      call(buyer,"PUT","/api/reviews/"+id,"{\"rating\":5,\"comment\":\"Updated\"}",200);
      assertEquals(5,jdbc.queryForObject("SELECT rating FROM reviews WHERE id=?",Integer.class,id));
      call(buyer,"DELETE","/api/reviews/"+id,null,204);call(guest,"GET","/api/reviews/"+id,null,404);
    }
    @Test void rejectsInvalidAndSpoofedRatings() throws Exception {
      property();Client buyer=new Client();buyer.login("buyer");
      for(String rating:new String[]{"0","6","1.5","null","2147483648"})call(buyer,"POST","/api/reviews",review().replace("\"rating\":4","\"rating\":"+rating),400);
      call(buyer,"POST","/api/reviews",review().replace("Good home"," "),400);
      call(buyer,"POST","/api/reviews",review().replace("\"comment\"","\"reviewerId\":\"fake\",\"comment\""),400);
      call(buyer,"POST","/api/reviews",review().replace(PROPERTY,"bad"),400);
    }
    @Test void adminDeleteAndReferencedAccounts() throws Exception {
      property();Client buyer=new Client();buyer.login("buyer");Client admin=new Client();admin.login("admin");
      String id=value(call(buyer,"POST","/api/reviews",review(),201),"id");
      call(admin,"DELETE","/api/users/"+BUYER,null,409);call(admin,"PUT","/api/users/"+BUYER+"/role","{\"role\":\"SELLER\"}",409);
      call(admin,"DELETE","/api/reviews/"+id,null,204);call(admin,"PUT","/api/users/"+BUYER+"/role","{\"role\":\"SELLER\"}",200);
    }
    @Test void unrelatedSellerMayReviewAndDeletedSessionIsRejected() throws Exception {
      property();Client admin=new Client();admin.login("admin");String uid=value(call(admin,"POST","/api/users",create("seller2","SELLER"),201),"id");
      Client other=new Client();other.login("seller2");String id=value(call(other,"POST","/api/reviews",review(),201),"id");
      call(other,"DELETE","/api/reviews/"+id,null,204);call(admin,"DELETE","/api/users/"+uid,null,204);
      other.refresh();call(other,"POST","/api/reviews",review(),401);
    }

}