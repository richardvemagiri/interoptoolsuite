package com.ecw.dashboard.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@RequestMapping("/dashboard")
@RequestMapping("${suite.dashboard.base-path:/dashboard}")
public class DashboardController {

    @GetMapping
    public String getHomePage(){
        return "sample-page";

    }

    @GetMapping("/")
    public String redirectRoot() {
//        return "redirect:/dashboard";
        return "forward:/dashboard";
    }


    @GetMapping("/content/{tool}")
    public String getToolContent(@PathVariable String tool) {
        return switch (tool) {
            case "ccdaviewer" -> "tools/ccda-viewer :: content";
//            case "deidtool" -> "tools/ccda-deid-tool :: content";
            case "deidtool" -> "forward:/deid-tool/";
            default -> "tools/not-found :: content";
        };
    }

//    @RequestMapping("/content/deidtool")
//    public String getDeIDToolHomePage(){
//        System.out.println("Yechi!");
//        return "deid-tool";
//    }


}
