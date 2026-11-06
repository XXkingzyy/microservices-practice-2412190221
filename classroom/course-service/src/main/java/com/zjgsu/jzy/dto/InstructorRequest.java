package com.zjgsu.jzy.dto;

import jakarta.validation.constraints.*;

/** 创建教师请求（嵌套在课程请求中） */
public record InstructorRequest(
    @NotBlank String id,
    @NotBlank String name,
    @Email String email
) {}
