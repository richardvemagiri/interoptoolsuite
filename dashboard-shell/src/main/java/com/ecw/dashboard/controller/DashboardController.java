package com.ecw.dashboard.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {

    @RequestMapping("/")
    public String getHomePage(){
        return "sample-page";

    }

    @GetMapping("/content/{tool}")
    public String getToolContent(@PathVariable String tool) {
        return switch (tool) {
            case "ccdaviewer" -> "tools/ccda-viewer :: content";
            case "deidtool" -> "tools/ccda-deid-tool :: content";
            default -> "tools/not-found :: content";
        };
    }

//    @RequestMapping("/content/deidtool")
//    public String getDeIDToolHomePage(){
//        System.out.println("Yechi!");
//        return "deid-tool";
//    }


}
