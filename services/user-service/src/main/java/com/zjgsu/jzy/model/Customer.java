package com.zjgsu.jzy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * 顾客实体：用户服务独立拥有的数据（userdb.customers）
 * 订单服务需要顾客信息时通过 HTTP 调用本服务获取，不直接访问本表
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private String id;                 // 顾客 ID（UUID）

    @Column(nullable = false)
    private String name;               // 顾客姓名

    @Column(nullable = false)
    private String phone;              // 联系电话

    @Column(nullable = false)
    private Instant createdAt;         // 注册时间

    protected Customer() {} // JPA 无参构造器

    public Customer(String id, String name, String phone, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public Instant getCreatedAt() { return createdAt; }
}
