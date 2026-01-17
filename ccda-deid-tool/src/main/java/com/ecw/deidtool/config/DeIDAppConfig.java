package com.ecw.deidtool.config;


import com.ecw.deidtool.interfaces.StorageService;
import com.ecw.deidtool.repository.DeIDConfigDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DeIDAppConfig {

    @Autowired
    DeIDConfigDAO deIDConfigDAO;

    @Bean
    CommandLineRunner init(StorageService storageService) {

//        log.debug("XPaths: " + DOMXmlHelper.getXPaths());
        return (args) -> {
            storageService.deleteAll();
//            storageService.init();
        };

    }

//    @Bean(initMethod ="init")
//    public Object loadDBConfig(){
//        return null;
//    }

//    @Bean(name = "dbConfigBean")
//    public List<DeIDDBConfig> getDBConfig(){
//        deIDConfigDAO.findAll().forEach(deIDDBConfig -> log.debug(deIDDBConfig.toString()));
//        return deIDConfigDAO.findAll();
//    }
}
