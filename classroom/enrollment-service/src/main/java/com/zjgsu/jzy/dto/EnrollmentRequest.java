package com.zjgsu.jzy.dto;

import jakarta.validation.constraints.NotBlank;

/** 选课请求 */
public record EnrollmentRequest(
    @NotBlank String studentId,
    @NotBlank String courseId
) {}
