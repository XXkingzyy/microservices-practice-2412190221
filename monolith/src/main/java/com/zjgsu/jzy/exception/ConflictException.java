package com.zjgsu.jzy.exception;

/** 业务冲突异常（重复选课/课程满员/编号重复） → 409 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
