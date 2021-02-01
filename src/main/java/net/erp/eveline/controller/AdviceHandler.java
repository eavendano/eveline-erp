package net.erp.eveline.controller;

import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.exception.RestError;
import net.erp.eveline.common.exception.ServiceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@ControllerAdvice
public class AdviceHandler {

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public RestError handleServiceException(final ServiceException se, final HttpServletResponse response) {
        return handleExceptionTraversingCause(se, response)
                .orElseGet(() -> {
                    response.setStatus(500);
                    return new RestError(500, se.getMessage());
                });
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public RestError handleNotFoundException(final NotFoundException nfe, final HttpServletResponse response) {
        response.setStatus(404);
        return new RestError(404, nfe.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public RestError handleBadRequestException(final BadRequestException bre, final HttpServletResponse response) {
        response.setStatus(400);
        return new RestError(400, bre.getMessage());
    }

    private Optional<RestError> handleExceptionTraversingCause(final ServiceException se, final HttpServletResponse response) {
        final Optional<Throwable> rootCauseOptional = Optional.ofNullable(ExceptionUtils.getRootCause(se));
        if (rootCauseOptional.isPresent()) {
            final Throwable rootCause = rootCauseOptional.get();
            if (rootCause instanceof NotFoundException) {
                return Optional.of(handleNotFoundException((NotFoundException) rootCause, response));
            } else if (rootCause instanceof BadRequestException) {
                return Optional.of(handleBadRequestException((BadRequestException) rootCause, response));
            }
        }
        return Optional.empty();
    }
}
