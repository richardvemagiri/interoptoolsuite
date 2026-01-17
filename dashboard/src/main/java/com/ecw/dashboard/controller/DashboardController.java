package com.ecw.dashboard.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {

    @RequestMapping("/")
    public String getHomePage(){
        return "sample-page";

    }

//    @RequestMapping("/content/deidtool")
//    public String getDeIDToolHomePage(){
//        System.out.println("Yechi!");
//        return "deid-tool";
//    }


}
