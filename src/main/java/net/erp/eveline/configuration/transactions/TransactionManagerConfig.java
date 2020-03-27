package net.erp.eveline.configuration.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TransactionManagerConfig {

    private PlatformTransactionManager transactionManager;

    @Qualifier("writeTransactionTemplate")
    @Bean("writeTransactionTemplate")
    public TransactionTemplate getWriteTransactionTemplate() {

        TransactionTemplate writeTransactionTemplate = new TransactionTemplate(transactionManager);
        writeTransactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        writeTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        writeTransactionTemplate.setReadOnly(false);
        return writeTransactionTemplate;
    }

    @Qualifier("readOnly")
    @Bean("readOnlyTransactionTemplate")
    public TransactionTemplate getReadOnlyTransactionTemplate() {
        TransactionTemplate readOnlyTransactionTemplate = new TransactionTemplate(transactionManager);
        readOnlyTransactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        readOnlyTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        readOnlyTransactionTemplate.setReadOnly(true);
        return readOnlyTransactionTemplate;
    }

    @Autowired
    public void setTransactionManager(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
