package com.github.ratelimiter.core.exceptions;

/**
 * Created by Administrator on 2015/8/10.
 */
public class UpdateLimiterException extends RuntimeException {
    public UpdateLimiterException() {
    }

    public UpdateLimiterException(String message) {
        super(message);
    }

    public UpdateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateLimiterException(Throwable cause) {
        super(cause);
    }

    public UpdateLimiterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
