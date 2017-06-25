package com.rate.limiter.core.exceptions;

/**
 * Created by Administrator on 2015/8/10.
 */
public class InitLimiterException extends RuntimeException {
    public InitLimiterException() {
    }

    public InitLimiterException(String message) {
        super(message);
    }

    public InitLimiterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitLimiterException(Throwable cause) {
        super(cause);
    }

    public InitLimiterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
