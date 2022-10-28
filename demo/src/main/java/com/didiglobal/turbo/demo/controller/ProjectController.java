package com.didiglobal.turbo.demo.controller;

import com.didiglobal.turbo.demo.service.AfterSaleServiceImpl;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
@RequestMapping("/api/pro")
public class ProjectController {

    @Resource
    private AfterSaleServiceImpl service;

    @GetMapping(value = "/run")
    public String run() {
        service.run();
        return String.valueOf(LocalDateTime.now());
    }

}
