package com.zjgsu.jzy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * 菜品实体：菜单服务独立拥有的数据（menudb.dishes）
 * 订单服务下单时通过 HTTP 调用本服务获取菜品信息，不直接访问本表
 */
@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    private String id;                     // 菜品 ID（UUID）

    @Column(nullable = false)
    private String name;                   // 菜品名称

    @Column(nullable = false)
    private String category;               // 菜品分类

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;              // 单价

    @Column(nullable = false)
    private boolean available;             // 是否可售

    protected Dish() {} // JPA 无参构造器

    public Dish(String id, String name, String category, BigDecimal price, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public boolean isAvailable() { return available; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
}
