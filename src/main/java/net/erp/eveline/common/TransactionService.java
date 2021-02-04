package net.erp.eveline.common;

import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.RetryableException;
import net.erp.eveline.common.exception.ServiceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.OptimisticLockException;
import java.util.concurrent.ExecutionException;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private TransactionTemplate readOnlyTransactionTemplate;
    private TransactionTemplate writeTransactionTemplate;
    private RetryTemplate retryTemplate;

    public <T, B> T performWriteTransaction(final TransactionCallback<T> callback, final B parameter) {
        return performTransaction(writeTransactionTemplate, callback, parameter);
    }

    public <B> void performWriteTransactionWithoutResult(final TransactionCallback<Object> callbackWithoutResult, final B parameter) {
        performTransactionWithoutResult(writeTransactionTemplate, callbackWithoutResult, parameter);
    }

    public <T, B> T performReadOnlyTransaction(final TransactionCallback<T> callback, final B parameter) {
        return performTransaction(readOnlyTransactionTemplate, callback, parameter);
    }

    private <T, B> T performTransaction(final TransactionTemplate transactionTemplate, final TransactionCallback<T> callback, final B parameter) {
        var parameterSanitized = (parameter == null) ? "null" : parameter.toString();
        try {
            return retryTemplate.execute((RetryCallback<T, Throwable>) context -> {
                try {
                    logger.info("Executing transaction for: {}", parameterSanitized);
                    return transactionTemplate.execute(callback);
                } catch (final Throwable ex) {
                    var message = "";
                    if (canRetry(ex)) {
                        message = String.format("Transaction is going to be retried for: %s | Cause: %s", parameterSanitized, ex.getMessage());
                        logger.warn(message, ex);
                        throw new RetryableException(message, ex);
                    } else {
                        message = String.format("Transaction is not possible to retry for: %s | Cause: %s", parameterSanitized, ex.getMessage());
                        logger.warn(message, ex);
                        throw new NonRetryableException(message, ex);
                    }
                }
            });
        } catch (RetryableException | NonRetryableException ex) {
            var message = String.format("Unable to process request probably due to exhaust for: %s", parameterSanitized);
            logger.warn(message, ex);
            if (ex instanceof RetryableException) {
                throw new RetryableException(message, ex);
            } else {
                throw new NonRetryableException(message, ex);
            }
        } catch (Throwable ex) {
            var message = String.format("Unexpected exception occurred. Unable to perform transaction for: %s | Cause: %s", parameterSanitized, ex.getMessage());
            logger.warn(message, ex);
            throw new ServiceException(message, ex);
        }
    }

    private <B> Void performTransactionWithoutResult(final TransactionTemplate transactionTemplate, final TransactionCallback<Object> callback, final B parameter) {
        var parameterSanitized = (parameter == null) ? "null" : parameter.toString();
        try {
            return retryTemplate.execute((RetryCallback<Void, Throwable>) context -> {
                try {
                    logger.info("Executing transaction without result for: {}", parameterSanitized);
                    transactionTemplate.execute(callback);
                    return null;
                } catch (final Throwable ex) {
                    var message = "";
                    if (canRetry(ex)) {
                        message = String.format("Transaction is going to be retried for: %s | Cause: %s", parameterSanitized, ex.getMessage());
                        logger.warn(message, ex);
                        throw new RetryableException(message, ex);
                    } else {
                        message = String.format("Transaction is not possible to retry for: %s | Cause: %s", parameterSanitized, ex.getMessage());
                        logger.warn(message, ex);
                        throw new NonRetryableException(message, ex);
                    }
                }
            });
        } catch (RetryableException | NonRetryableException ex) {
            var message = String.format("Unable to process request probably due to exhaust for: %s", parameterSanitized);
            logger.warn(message, ex);
            if (ex instanceof RetryableException) {
                throw new RetryableException(message, ex);
            } else {
                throw new NonRetryableException(message, ex);
            }
        } catch (Throwable ex) {
            var message = String.format("Unexpected exception occurred. Unable to perform transaction for: %s | Cause: %s", parameterSanitized, ex.getMessage());
            logger.warn(message, ex);
            throw new ServiceException(message, ex);
        }
    }

    private static boolean canRetry(final DataIntegrityViolationException ex) {
        return ex != null &&
                ex.getCause() instanceof ConstraintViolationException;
    }

    private static boolean canRetry(final Throwable ex) {
        return (ex instanceof DataIntegrityViolationException && canRetry((DataIntegrityViolationException) ex)) ||
                ex instanceof ObjectOptimisticLockingFailureException ||
                ex instanceof OptimisticLockException ||
                ex instanceof CannotCreateTransactionException ||
                (ex instanceof ExecutionException && canRetry(ex.getCause()));
    }

    @Autowired
    public void setReadOnlyTransactionTemplate(final TransactionTemplate readOnlyTransactionTemplate) {
        this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
    }

    @Autowired
    public void setWriteTransactionTemplate(final TransactionTemplate writeTransactionTemplate) {
        this.writeTransactionTemplate = writeTransactionTemplate;
    }

    @Autowired
    public void setRetryTemplate(final RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }
}
