package net.erp.eveline.common.exception;

public class NonRetryableException extends ServiceException {

    public NonRetryableException(String message) {
        super(message);
    }

    public NonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
