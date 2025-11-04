package com.gpp.servisoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class homeController {

    @GetMapping("/home")
    public String info()
    {
        return "home";
    }
}
