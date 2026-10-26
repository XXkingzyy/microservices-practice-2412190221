package com.zjgsu.jzy.exception;

/**
 * 依赖服务不可用（连接失败/超时/服务端异常） → 503
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
