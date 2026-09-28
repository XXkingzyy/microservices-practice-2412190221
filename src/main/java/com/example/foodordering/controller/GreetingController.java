package com.example.foodordering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问候接口：验证 Spring Boot 项目可以正常启动并提供 REST 服务。
 */
@RestController
public class GreetingController {

    /**
     * GET /hello 返回欢迎问候语。
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, welcome to the Food Ordering System!";
    }
}
