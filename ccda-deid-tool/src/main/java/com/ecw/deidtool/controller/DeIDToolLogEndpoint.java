package com.ecw.deidtool.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Path;

@Controller
@RequestMapping("${suite.deid.base-path:/deid-tool}/actuator")
public class DeIDToolLogEndpoint {

    private static final Path LOG_PATH = Path.of("logs", "deid-tool.log");

    @GetMapping("/logfile")
    @ResponseBody
    public ResponseEntity<Resource> logfile() {
        Resource resource = new FileSystemResource(LOG_PATH);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

}
