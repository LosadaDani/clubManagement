package com.managementClub.managementClub.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicWebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping({"/el-club", "/el-club/"})
    public String club() {
        return "club";
    }
}
