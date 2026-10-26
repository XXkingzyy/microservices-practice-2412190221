package com.zjgsu.jzy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 订单服务入口：负责订单（Order）数据的独立拥有与 REST 暴露。
 * 下单前通过 RestTemplate 跨服务校验顾客（user-service）与菜品（menu-service）。
 */
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
