package com.blocklang.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.blocklang.system.constant.WebSite;

@Controller
public class HomeController {
	// Match everything without a suffix (so not a static resource)
    @GetMapping(value = "/**/{path:[^.]*}")       
    public String toHome() {
        // Forward to home page so that route is preserved.
        return "forward:" + WebSite.HOME_URL;
    }
}
