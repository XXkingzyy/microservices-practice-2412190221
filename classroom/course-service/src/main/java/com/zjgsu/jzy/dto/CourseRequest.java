package com.zjgsu.jzy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/** 创建课程请求 */
public record CourseRequest(
    @NotBlank String code,
    @NotBlank String title,
    @Valid @NotNull InstructorRequest instructor,
    @Valid @NotNull ScheduleSlotRequest schedule,
    @Min(1) int capacity
) {}
