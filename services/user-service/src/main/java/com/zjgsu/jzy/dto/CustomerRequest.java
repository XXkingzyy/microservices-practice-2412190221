package com.zjgsu.jzy.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 注册顾客请求
 */
public record CustomerRequest(
        @NotBlank(message = "姓名不能为空") String name,
        @NotBlank(message = "电话不能为空") String phone) {
}
