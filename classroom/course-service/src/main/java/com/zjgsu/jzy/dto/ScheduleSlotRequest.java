package com.zjgsu.jzy.dto;

import jakarta.validation.constraints.NotBlank;

/** 创建上课时段请求（嵌套在课程请求中） */
public record ScheduleSlotRequest(
    @NotBlank String dayOfWeek,
    @NotBlank String startTime,
    @NotBlank String endTime
) {}
