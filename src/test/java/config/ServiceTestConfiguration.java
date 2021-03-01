package config;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.RetryableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceTestConfiguration {

    @Qualifier("writeTransactionTemplate")
    @Bean(name = "writeTransactionTemplate")
    public TransactionTemplate getWriteTransactionTemplate() {

        TransactionTemplate writeTransactionTemplate = new TransactionTemplate(new FakePlatformTransactionManager());
        writeTransactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        writeTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        writeTransactionTemplate.setReadOnly(false);
        return writeTransactionTemplate;
    }

    @Qualifier("readOnlyTransactionTemplate")
    @Bean(name = "readOnlyTransactionTemplate")
    public TransactionTemplate getReadOnlyTransactionTemplate() {
        TransactionTemplate readOnlyTransactionTemplate = new TransactionTemplate(new FakePlatformTransactionManager());
        readOnlyTransactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        readOnlyTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        readOnlyTransactionTemplate.setReadOnly(true);
        return readOnlyTransactionTemplate;
    }

    @Bean(name = "retryTransactionTemplate")
    @Qualifier("retryTransactionTemplate")
    public RetryTemplate getRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Backoff Policy (*in seconds): 3, 6, 9, 12, 15
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(3000L);
        backOffPolicy.setMultiplier(1);//This multiplier was updated to speedup testing and project building.
        backOffPolicy.setMaxInterval(15000);

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(RetryableException.class, true);
        retryableExceptions.put(NonRetryableException.class, false);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(4, retryableExceptions);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }

    @Bean
    public TransactionService getTransactionService() {
        return new TransactionService();
    }


    private static class FakePlatformTransactionManager extends AbstractPlatformTransactionManager {

        @Override
        protected Object doGetTransaction() throws TransactionException {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        }
    }
}

