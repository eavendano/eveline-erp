package net.erp.eveline.service;

import net.erp.eveline.common.exception.BadRequestException;

import java.util.List;
import java.util.function.Predicate;

public class BaseService {
    protected <T> void validate(final T object, final Predicate<T> predicate, final String invalidMessage) {
        if (!predicate.test(object)) throw new BadRequestException(invalidMessage);
    }

    protected <T> void validate(final T object, final Predicate<T> predicate, final List<String> errors) {
        if (!predicate.test(object)) throw new BadRequestException(String.join(" | ", errors));
    }
}
