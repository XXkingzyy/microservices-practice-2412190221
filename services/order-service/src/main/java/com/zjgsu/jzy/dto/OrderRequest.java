package com.zjgsu.jzy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 下单请求：customerId 引用 user-service 的顾客；明细只带 dishId 与数量
 */
public record OrderRequest(
        @NotBlank(message = "顾客 ID 不能为空") String customerId,
        @NotEmpty(message = "订单明细不能为空")
        @Valid
        List<ItemRequest> items) {

    public record ItemRequest(
            @NotBlank(message = "菜品 ID 不能为空") String dishId,
            @NotNull(message = "数量不能为空") @Min(value = 1, message = "数量至少为 1") Integer quantity) {
    }
}
