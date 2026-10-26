package com.zjgsu.jzy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

/**
 * 订单实体：订单服务独立拥有的数据（orderdb.food_orders）
 * 只保存顾客 ID 与菜品 ID（跨服务关联），不建立跨库外键；
 * 姓名与菜品名/单价为下单时的快照，来自跨服务 HTTP 调用结果
 */
@Entity
@Table(name = "food_orders")
public class Order {

    @Id
    private String id;                          // 订单 ID（UUID）

    @Column(nullable = false)
    private String customerId;                  // 顾客 ID（user-service 拥有，仅存 ID）

    @Column(nullable = false)
    private String customerName;                // 顾客姓名（下单时快照，来自 user-service）

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;                 // 订单状态

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;              // 订单总价（后端按明细累加）

    @Column(nullable = false)
    private Instant createdAt;                  // 下单时间

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>(); // 订单明细

    protected Order() {} // JPA 无参构造器

    public Order(String id, String customerId, String customerName, OrderStatus status,
                 BigDecimal totalPrice, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    /** 添加订单明细 */
    public void addItem(OrderItem item) {
        items.add(item);
    }

    /** 更新订单总价（下单时后端累加得出） */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isPending() { return status == OrderStatus.PENDING; }
    public boolean isConfirmed() { return status == OrderStatus.CONFIRMED; }

    public void confirm() { this.status = OrderStatus.CONFIRMED; }
    public void complete() { this.status = OrderStatus.COMPLETED; }
    public void cancel() { this.status = OrderStatus.CANCELLED; }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
}
