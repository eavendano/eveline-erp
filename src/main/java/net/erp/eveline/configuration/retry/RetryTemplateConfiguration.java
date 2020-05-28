package net.erp.eveline.configuration.retry;

import net.erp.eveline.common.exception.NonRetryableException;
import net.erp.eveline.common.exception.RetryableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryTemplateConfiguration {

    @Bean
    @Qualifier("retryTemplate")
    public RetryTemplate getRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Backoff Policy (*in seconds): 5, 10, 20, 40, 60
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(5000L);
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setMaxInterval(60000);

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(RetryableException.class, true);
        retryableExceptions.put(NonRetryableException.class, false);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(4, retryableExceptions);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }

}
