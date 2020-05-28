package net.erp.eveline.common.exception;

public class BadRequestException extends ServiceException {
    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
