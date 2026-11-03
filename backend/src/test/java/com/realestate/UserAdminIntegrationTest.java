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
class UserAdminIntegrationTest {
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

    @Test void publicRegistrationHashesPasswordAndRejectsSpoofing() throws Exception {
        Client client=new Client();
        call(client,"POST","/api/auth/register",create("amal","BUYER"),403);
        client.refresh();String body=call(client,"POST","/api/auth/register",create("amal","BUYER"),201);
        String id=value(body,"id");assertFalse(body.contains("password"));
        String hash=jdbc.queryForObject("SELECT password_hash FROM users WHERE id=?",String.class,id);
        assertNotEquals(PASSWORD,hash);assertTrue(encoder.matches(PASSWORD,hash));
        assertTrue(call(client,"GET","/api/auth/me",null,200).contains("\"user\":null"));
        call(client,"POST","/api/auth/register",create("AMAL","SELLER"),409);
        call(client,"POST","/api/auth/register",create("evil","ADMIN"),403);
        call(client,"POST","/api/auth/register",create("new","BUYER").replace("\"role\"","\"id\":\"fake\",\"role\""),400);
        call(client,"POST","/api/auth/register",create("new","UNKNOWN"),400);
        call(client,"POST","/api/auth/register",create("new","BUYER").replace(PASSWORD,"short"),400);
    }
    @Test void loginRotatesSessionTokenAndLogoutClearsAccess() throws Exception {
        Client buyer=new Client();buyer.refresh();String previous=buyer.token;
        String body=call(buyer,"POST","/api/auth/login","{\"username\":\"BUYER\",\"password\":\""+PASSWORD+"\"}",200);
        String next=value(body,"csrfToken");assertNotEquals(previous,next);
        assertFalse(body.contains("passwordHash"));assertTrue(body.contains(BUYER));
        call(buyer,"PUT","/api/users/"+BUYER,profile("updated"),403); // stale token
        buyer.token=next;call(buyer,"PUT","/api/users/"+BUYER,profile("updated"),200);
        call(buyer,"POST","/api/auth/logout",null,204);
        call(buyer,"GET","/api/users/"+BUYER,null,401);
        buyer.refresh();assertTrue(call(buyer,"GET","/api/auth/me",null,200).contains("\"user\":null"));
    }
    @Test void invalidLoginIsGenericAndDoesNotGrantSession() throws Exception {
        Client c=new Client();c.refresh();
        String wrong=call(c,"POST","/api/auth/login","{\"username\":\"buyer\",\"password\":\"wrong\"}",401);
        String missing=call(c,"POST","/api/auth/login","{\"username\":\"missing\",\"password\":\"wrong\"}",401);
        assertEquals(wrong,missing);assertTrue(call(c,"GET","/api/auth/me",null,200).contains("\"user\":null"));
    }
    @Test void adminCrudAndOrdinaryAccountRestrictions() throws Exception {
        Client admin=new Client();admin.login("admin");Client buyer=new Client();buyer.login("buyer");
        call(buyer,"GET","/api/users",null,403);call(buyer,"GET","/api/users/"+SELLER,null,403);
        call(buyer,"POST","/api/users",create("staff","ADMIN"),403);
        String id=value(call(admin,"POST","/api/users",create("newadmin","ADMIN"),201),"id");
        assertTrue(call(admin,"GET","/api/users",null,200).contains(id));
        call(admin,"PUT","/api/users/"+id,profile("renamed"),200);
        call(admin,"PUT","/api/users/"+id+"/role","{\"role\":\"SELLER\"}",200);
        call(buyer,"PUT","/api/users/"+BUYER+"/role","{\"role\":\"ADMIN\"}",403);
        call(admin,"DELETE","/api/users/"+ADMIN,null,403);
        call(admin,"PUT","/api/users/"+ADMIN+"/role","{\"role\":\"BUYER\"}",409);
        call(admin,"DELETE","/api/users/"+id,null,204);
        call(admin,"GET","/api/users/"+id,null,404);
    }
    @Test void linkedAccountsAndUnknownProfileFieldsAreProtected() throws Exception {
        jdbc.update("INSERT INTO properties VALUES(?,?,?,?,?,?,?,?,?)",PROPERTY,SELLER,"Home","Quiet home","Colombo","Green Road",new java.math.BigDecimal("1000.00"),"HOUSE","AVAILABLE");
        Client admin=new Client();admin.login("admin");
        call(admin,"DELETE","/api/users/"+SELLER,null,409);
        call(admin,"PUT","/api/users/"+SELLER+"/role","{\"role\":\"BUYER\"}",409);
        Client buyer=new Client();buyer.login("buyer");
        call(buyer,"PUT","/api/users/"+BUYER,profile("buyer").replace("\"phone\"","\"role\":\"ADMIN\",\"phone\""),400);
    }
    @Test void passwordChangeInvalidatesEveryTrackedSession() throws Exception {
        Client one=new Client();one.login("buyer");Client two=new Client();two.login("buyer");
        String change="{\"currentPassword\":\""+PASSWORD+"\",\"newPassword\":\"NewPassword456!\"}";
        call(one,"PUT","/api/users/"+BUYER+"/password",change.replace(PASSWORD,"wrong"),400);
        call(one,"PUT","/api/users/"+BUYER+"/password",change,204);
        call(one,"GET","/api/users/"+BUYER,null,401);call(two,"GET","/api/users/"+BUYER,null,401);
        one.refresh();call(one,"POST","/api/auth/login","{\"username\":\"buyer\",\"password\":\""+PASSWORD+"\"}",401);
        String result=call(one,"POST","/api/auth/login","{\"username\":\"buyer\",\"password\":\"NewPassword456!\"}",200);assertTrue(result.contains(BUYER));
        Client admin=new Client();admin.login("admin");call(admin,"PUT","/api/users/"+BUYER+"/password",change,403);
    }
    @Test void roleChangesAffectExistingSessionsAndDeletionInvalidatesThem() throws Exception {
        Client buyer=new Client();buyer.login("buyer");Client admin=new Client();admin.login("admin");
        call(admin,"PUT","/api/users/"+BUYER+"/role","{\"role\":\"ADMIN\"}",200);
        call(buyer,"GET","/api/users",null,200);
        call(admin,"PUT","/api/users/"+BUYER+"/role","{\"role\":\"BUYER\"}",200);
        call(buyer,"GET","/api/users",null,403);
        call(admin,"DELETE","/api/users/"+BUYER,null,204);
        call(buyer,"GET","/api/users/"+BUYER,null,401);
    }
    @Test void concurrentSelfDemotionsKeepOneAdmin() throws Exception {
        final String second="10000000-0000-0000-0000-000000000006";seed(second,"secondadmin","ADMIN");
        final Client firstClient=new Client();firstClient.login("admin");final Client secondClient=new Client();secondClient.login("secondadmin");
        ExecutorService pool=Executors.newFixedThreadPool(2);
        try {
            Future<Integer> a=pool.submit(new Callable<Integer>() {public Integer call() throws Exception {return firstClient.send("PUT","/api/users/"+ADMIN+"/role","{\"role\":\"BUYER\"}").statusCode();}});
            Future<Integer> b=pool.submit(new Callable<Integer>() {public Integer call() throws Exception {return secondClient.send("PUT","/api/users/"+second+"/role","{\"role\":\"BUYER\"}").statusCode();}});
            int x=a.get();int y=b.get();assertTrue(x==200 && y==409 || x==409 && y==200,"Results "+x+", "+y);
            assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE role='ADMIN'",Integer.class));
        } finally {pool.shutdownNow();}
    }
    @Test void controlledBootstrapCreatesOneHashedAdminAndRefusesRepeat() {
        jdbc.update("DELETE FROM users");bootstrap.createFirstAdmin("firstadmin",PASSWORD,"first@example.com","0771234567");
        assertEquals(1,jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE role='ADMIN'",Integer.class));
        String hash=jdbc.queryForObject("SELECT password_hash FROM users WHERE username='firstadmin'",String.class);assertTrue(encoder.matches(PASSWORD,hash));
        boolean refused=false;try{bootstrap.createFirstAdmin("second",PASSWORD,"second@example.com","0771234567");}catch(ModuleException e){refused=e.getStatus().value()==409;}assertTrue(refused);
    }
}
