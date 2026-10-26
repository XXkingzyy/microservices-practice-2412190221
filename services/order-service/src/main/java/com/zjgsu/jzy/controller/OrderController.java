package com.zjgsu.jzy.controller;

import com.zjgsu.jzy.dto.OrderRequest;
import com.zjgsu.jzy.model.Order;
import com.zjgsu.jzy.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * 订单 REST 接口（/api/orders）
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 下单（触发跨服务校验顾客与菜品）→ 201 / 404 / 409 / 503 */
    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest req,
                                            UriComponentsBuilder uriBuilder) {
        Order order = orderService.placeOrder(req.customerId(), req.items());
        return ResponseEntity.created(uriBuilder.path("/api/orders/{id}")
                .buildAndExpand(order.getId()).toUri()).body(order);
    }

    /** 查询所有订单 → 200 */
    @GetMapping
    public List<Order> list() {
        return orderService.findAll();
    }

    /** 按 ID 查询订单 → 200 */
    @GetMapping("/{id}")
    public Order getById(@PathVariable String id) {
        return orderService.findById(id);
    }

    /** 确认订单（仅 PENDING）→ 200 */
    @PostMapping("/{id}/confirm")
    public Order confirm(@PathVariable String id) {
        return orderService.confirm(id);
    }

    /** 完成订单（仅 CONFIRMED）→ 200 */
    @PostMapping("/{id}/complete")
    public Order complete(@PathVariable String id) {
        return orderService.complete(id);
    }

    /** 取消订单（仅 PENDING）→ 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable String id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
