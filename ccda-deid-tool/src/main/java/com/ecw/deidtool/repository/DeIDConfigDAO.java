package com.ecw.deidtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeIDConfigDAO extends JpaRepository<DeIDDBConfig, Integer> {

    //TODO: Implement DB caching
//    @Cacheable(cacheNames = "dbconfig", key="#xpath")
    List<DeIDDBConfig> findAllByIsDeleted(int delflag);
}
