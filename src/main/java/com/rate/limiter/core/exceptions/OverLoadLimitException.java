package com.rate.limiter.core.exceptions;

/**
 * Created by Administrator on 2015/8/10.
 */
public class OverLoadLimitException extends RuntimeException {
    public OverLoadLimitException() {
    }

    public OverLoadLimitException(String message) {
        super(message);
    }

    public OverLoadLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverLoadLimitException(Throwable cause) {
        super(cause);
    }

    public OverLoadLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
