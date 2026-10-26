package com.zjgsu.jzy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 新增菜品请求
 */
public record DishRequest(
        @NotBlank(message = "菜品名称不能为空") String name,
        @NotBlank(message = "菜品分类不能为空") String category,
        @NotNull(message = "单价不能为空") @DecimalMin(value = "0.00", message = "单价不能为负") BigDecimal price,
        @NotNull(message = "是否可售不能为空") Boolean available) {
}
