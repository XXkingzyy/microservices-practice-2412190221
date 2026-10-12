package com.zjgsu.jzy.exception;

/** 资源不存在异常 → 404 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
