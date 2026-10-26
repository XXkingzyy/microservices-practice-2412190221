package com.zjgsu.jzy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * 订单明细实体：记录下单时的菜品快照（名称与单价），菜品改价不影响历史订单
 */
@Entity
@Table(name = "food_order_items")
public class OrderItem {

    @Id
    private String id;                         // 明细 ID（UUID）

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;                       // 所属订单

    @Column(nullable = false)
    private String dishId;                     // 菜品 ID（menu-service 拥有，仅存 ID）

    @Column(nullable = false)
    private String dishName;                   // 菜品名称（下单时快照）

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;              // 下单时单价快照

    @Column(nullable = false)
    private int quantity;                      // 数量

    protected OrderItem() {} // JPA 无参构造器

    public OrderItem(String id, Order order, String dishId, String dishName,
                     BigDecimal unitPrice, int quantity) {
        this.id = id;
        this.order = order;
        this.dishId = dishId;
        this.dishName = dishName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** 该明细小计 = 单价 × 数量 */
    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getId() { return id; }
    @JsonIgnore // 防止 Order ↔ OrderItem 双向关联 JSON 无限递归，只序列化明细本身
    public Order getOrder() { return order; }
    public String getDishId() { return dishId; }
    public String getDishName() { return dishName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
}
