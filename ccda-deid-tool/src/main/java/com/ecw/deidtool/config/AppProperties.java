package com.ecw.deidtool.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="ccda")
public class AppProperties {

//    private List<String> xpaths;
//    private String overwritePhrase;
    private String fileNameAppend;
    private Map<String,String> namespaces;


}
