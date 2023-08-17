package com.babyblue.framework.exception;

public class MyIoCException extends RuntimeException {

    public MyIoCException(String message) {
        super(message);
    }

    public MyIoCException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyIoCException(Throwable cause) {
        super(cause);
    }
}
