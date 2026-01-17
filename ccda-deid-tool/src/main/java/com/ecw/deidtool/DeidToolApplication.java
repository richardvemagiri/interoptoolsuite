package com.ecw.deidtool;

import com.ecw.deidtool.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class DeidToolApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        log.debug("In Main()");

        SpringApplication.run(DeidToolApplication.class, args);

    }

//    @Bean
//    CommandLineRunner init(StorageService storageService) {
//        log.info("In Init");
//        return (args) -> {
//            storageService.deleteAll();
//            storageService.init();
//        };
//
//    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DeidToolApplication.class);
    }

}
