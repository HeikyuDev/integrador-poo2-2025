package com.gpp.servisoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/proximamente")
public class ProximamenteController {


    @GetMapping()
    public String proximamente() {
        return "proximamente";
    }
    
}
