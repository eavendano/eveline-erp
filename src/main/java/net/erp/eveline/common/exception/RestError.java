package net.erp.eveline.common.exception;

public class RestError {
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public RestError(int status, final String message) {
        this.status = status;
        this.message = message;
    }
}
