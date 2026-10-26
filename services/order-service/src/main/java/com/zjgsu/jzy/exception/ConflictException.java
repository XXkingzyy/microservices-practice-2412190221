package com.zjgsu.jzy.exception;

/**
 * 业务冲突 → 409
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
