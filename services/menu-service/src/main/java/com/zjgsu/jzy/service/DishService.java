package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Dish;
import com.zjgsu.jzy.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 菜品业务：菜单服务边界内的数据创建与查询
 */
@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    /** 新增菜品 */
    @Transactional
    public Dish create(String name, String category, BigDecimal price, boolean available) {
        Dish dish = new Dish(UUID.randomUUID().toString(), name, category, price, available);
        return dishRepository.save(dish);
    }

    /** 按 ID 查询菜品，不存在抛 404（订单服务跨服务校验时调用） */
    @Transactional(readOnly = true)
    public Dish findById(String id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("菜品不存在: " + id));
    }

    /** 查询全部菜品 */
    @Transactional(readOnly = true)
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }
}
