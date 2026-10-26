package com.zjgsu.jzy.dto;

import java.math.BigDecimal;

/**
 * user-service 返回的顾客信息（跨服务调用响应）
 */
public record CustomerResponse(String id, String name, String phone) {
}
