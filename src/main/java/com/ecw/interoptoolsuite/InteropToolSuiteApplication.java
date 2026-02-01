package com.ecw.interoptoolsuite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ecw")
public class InteropToolSuiteApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        log.debug("In Main()");
        SpringApplication.run(InteropToolSuiteApplication.class, args);

    }

}
