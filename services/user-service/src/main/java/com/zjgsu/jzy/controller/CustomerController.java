package com.zjgsu.jzy.controller;

import com.zjgsu.jzy.dto.CustomerRequest;
import com.zjgsu.jzy.model.Customer;
import com.zjgsu.jzy.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * 顾客 REST 接口（/api/customers）
 * GET /api/customers/{id} 同时作为订单服务跨服务校验顾客的入口
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /** 注册顾客 → 201 */
    @PostMapping
    public ResponseEntity<Customer> register(@Valid @RequestBody CustomerRequest req,
                                             UriComponentsBuilder uriBuilder) {
        Customer customer = customerService.register(req.name(), req.phone());
        return ResponseEntity.created(uriBuilder.path("/api/customers/{id}")
                .buildAndExpand(customer.getId()).toUri()).body(customer);
    }

    /** 按 ID 查询顾客 → 200 / 404 */
    @GetMapping("/{id}")
    public Customer getById(@PathVariable String id) {
        return customerService.findById(id);
    }

    /** 查询全部顾客 → 200 */
    @GetMapping
    public List<Customer> list() {
        return customerService.findAll();
    }
}
