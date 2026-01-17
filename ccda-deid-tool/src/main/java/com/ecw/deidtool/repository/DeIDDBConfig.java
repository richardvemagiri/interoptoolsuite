package com.ecw.deidtool.repository;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "deidconfig")
@Data
public class DeIDDBConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "xpath", unique = true, nullable = false)
    private String xPath;
    @Column(nullable = false)
    private String value;
    @Column(name = "delflag", columnDefinition = "integer default 0", nullable = false)
    private int isDeleted=0;
    @Column(nullable = false)
    private String category;
}
