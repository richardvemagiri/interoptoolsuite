package com.ecw.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.ecw")
public class DashboardApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
    }

}
