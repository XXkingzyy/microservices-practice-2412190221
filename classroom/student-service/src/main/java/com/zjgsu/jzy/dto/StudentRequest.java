package com.zjgsu.jzy.dto;

import jakarta.validation.constraints.NotBlank;

/** 创建学生请求 */
public record StudentRequest(
    @NotBlank String studentNumber,
    @NotBlank String name,
    String major
) {}
