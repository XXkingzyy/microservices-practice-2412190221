package com.zjgsu.jzy.model;

/**
 * 订单状态：PENDING → CONFIRMED → COMPLETED；PENDING 可 CANCELLED
 */
public enum OrderStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}
