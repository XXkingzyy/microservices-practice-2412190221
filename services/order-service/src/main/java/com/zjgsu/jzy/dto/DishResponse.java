package com.zjgsu.jzy.dto;

import java.math.BigDecimal;

/**
 * menu-service 返回的菜品信息（跨服务调用响应）
 */
public record DishResponse(String id, String name, String category,
                           BigDecimal price, boolean available) {
}
