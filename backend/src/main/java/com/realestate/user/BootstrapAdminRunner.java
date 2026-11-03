package com.realestate.user;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// One-time local setup; there is no public "make me ADMIN" endpoint.
@Component
@Profile("bootstrap-admin")
public class BootstrapAdminRunner implements ApplicationRunner {
    private final BootstrapAdminService service;
    private final Environment environment;
    public BootstrapAdminRunner(BootstrapAdminService service,Environment environment) { this.service=service;this.environment=environment; }
    @Override
    public void run(ApplicationArguments args) {
        service.createFirstAdmin(environment.getProperty("BOOTSTRAP_ADMIN_USERNAME"),
            environment.getProperty("BOOTSTRAP_ADMIN_PASSWORD"),
            environment.getProperty("BOOTSTRAP_ADMIN_EMAIL"),environment.getProperty("BOOTSTRAP_ADMIN_PHONE"));
        System.out.println("First ADMIN created. Stop and restart without the bootstrap-admin profile; clear bootstrap environment variables.");
    }
}
