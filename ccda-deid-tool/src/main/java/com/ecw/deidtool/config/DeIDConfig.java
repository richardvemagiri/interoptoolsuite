package com.ecw.deidtool.config;

import com.ecw.deidtool.repository.DeIDConfigDAO;
import com.ecw.deidtool.repository.DeIDDBConfig;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@Slf4j
@Configuration
@Data
@Getter
@EnableJpaRepositories(basePackages = "com.ecw.deidtool.repository")
public class DeIDConfig {

    private final DeIDConfigDAO deIDConfigDAO;
    private List<DeIDDBConfig> dbConfigTable;

//    public DeIDConfig(DeIDConfigDAO deIDConfigDAO) {
//        this.deIDConfigDAO = deIDConfigDAO;
//        dbConfigTable = deIDConfigDAO.findAll();
//        log.debug("dbConfig: " + dbConfigTable.toString());
//    }

}
