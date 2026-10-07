package com.pmt.ikportal;

import com.pmt.ikportal.config.DatabaseBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IkPortalApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(IkPortalApplication.class);
        application.addListeners(new DatabaseBootstrap());
        application.run(args);
    }
}
