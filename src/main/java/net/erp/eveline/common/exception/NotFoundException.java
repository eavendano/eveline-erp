package net.erp.eveline.common.exception;

public class NotFoundException extends ServiceException {
    public NotFoundException(final String message) {
        super(message);
    }

    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
