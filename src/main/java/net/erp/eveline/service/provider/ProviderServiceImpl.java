package net.erp.eveline.service.provider;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.ProviderMapper;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProviderRepository;
import net.erp.eveline.model.ProviderModel;
import net.erp.eveline.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.erp.eveline.common.mapper.ProviderMapper.toModel;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderIdValid;
import static net.erp.eveline.common.predicate.ProviderPredicates.providerIdInvalidMessage;

@Service
public class ProviderServiceImpl extends BaseService implements ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ProviderRepository providerRepository;
    private TransactionService transactionService;

    @Override
    public ProviderModel getProviderModel(final String providerId) {
        logger.info("Obtaining provider for id: {}", providerId);
        validate(providerId, isProviderIdValid(), providerIdInvalidMessage());
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Performing transaction for Id: {}", providerId);
            final Optional<Provider> optionalProvider = providerRepository.findById(providerId);
            if (optionalProvider.isEmpty()) {
                throw new NotFoundException(String.format("Unable to find a provider with the id specified: %s", providerId));
            }
            var provider = optionalProvider.get();
            logger.info("Retrieving provider info: {}", provider);
            return toModel(provider);
        }, providerId);
    }

    @Override
    public List<ProviderModel> getProviderModels() {
        logger.info("Obtaining provider list");
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Performing read transaction for providers");
            final List<ProviderModel> providers = toModel(providerRepository.findAll());
            logger.info("Retrieved all providers info");
            return providers;
        }, null);
    }

    @Autowired
    public void setProviderRepository(final ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Autowired

    public void setTransactionService(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
