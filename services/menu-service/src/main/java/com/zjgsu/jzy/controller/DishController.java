package com.zjgsu.jzy.controller;

import com.zjgsu.jzy.dto.DishRequest;
import com.zjgsu.jzy.model.Dish;
import com.zjgsu.jzy.service.DishService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * 菜品 REST 接口（/api/dishes）
 * GET /api/dishes/{id} 同时作为订单服务跨服务校验菜品的入口
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /** 新增菜品 → 201 */
    @PostMapping
    public ResponseEntity<Dish> create(@Valid @RequestBody DishRequest req,
                                       UriComponentsBuilder uriBuilder) {
        Dish dish = dishService.create(req.name(), req.category(), req.price(), req.available());
        return ResponseEntity.created(uriBuilder.path("/api/dishes/{id}")
                .buildAndExpand(dish.getId()).toUri()).body(dish);
    }

    /** 按 ID 查询菜品 → 200 / 404 */
    @GetMapping("/{id}")
    public Dish getById(@PathVariable String id) {
        return dishService.findById(id);
    }

    /** 查询全部菜品 → 200 */
    @GetMapping
    public List<Dish> list() {
        return dishService.findAll();
    }
}
