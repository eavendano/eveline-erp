package net.erp.eveline.service.provider;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProviderRepository;
import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProviderModel;
import net.erp.eveline.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.ProviderMapper.toEntity;
import static net.erp.eveline.common.mapper.ProviderMapper.toModel;
import static net.erp.eveline.common.predicate.ProviderPredicates.PROVIDER_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.ProviderPredicates.isActiveProviderModelValid;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderIdValid;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderModelValidForInsert;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderModelValidForUpdate;

@Service
public class ProviderServiceImpl extends BaseService implements ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ProviderRepository providerRepository;
    private TransactionService transactionService;

    @Override
    public Set<ProviderModel> findAll() {
        logger.info("Obtaining all providers.");
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Requesting all providers.");
            Set<Provider> providers = Set.copyOf(providerRepository.findAll());
            logger.info("Retrieved all providers successfully.");
            return toModel(providers);
        }, null);
    }

    @Override
    public ProviderModel getProviderModel(final String providerId) {
        logger.info("Obtaining provider for id: {}", providerId);
        validate(providerId, isProviderIdValid(), PROVIDER_ID_INVALID_MESSAGE);
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
    public ProviderModel upsertProviderModel(final ProviderModel providerModel) {
        logger.info("Upsert operation for model: {}", providerModel);
        requireNonNull(providerModel, "Model provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing upsert transaction for model: {}", providerModel);
            final var providerId = ofNullable(providerModel.getId());
            ProviderModel result;

            if (providerId.isPresent()) {
                // Try to perform the update
                validate(providerModel, isProviderModelValidForUpdate(errorList), errorList);

                final var optionalProvider = providerRepository.findById(providerId.get());
                if (optionalProvider.isEmpty()) {
                    throw new NotFoundException(String.format("Unable to update a provider with the id specified: %s", providerId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update provider: {}", providerModel);
                result = toModel(providerRepository.save(toEntity(providerModel)));
                logger.info("Successful update operation for provider: {}", providerModel);

            } else {
                // Try to perform insert if the rest of the values is valid
                validate(providerModel, isProviderModelValidForInsert(errorList), errorList);
                logger.info("Preparing to insert provider: {}", providerModel);
                final Provider provider = toEntity(providerModel);
                provider.setProviderId("p" + String.format("%05d", providerRepository.getProvideIdNextVal()));
                result = toModel(providerRepository.save(provider));
                logger.info("Successful insert operation for provider: {}", providerModel);
            }

            logger.info("Upsert operation completed for model: {}", providerModel);
            return result;
        }, providerModel);
    }

    @Override
    public ProviderModel activateProvider(final ActiveProviderModel activeProviderModel) {
        logger.info("Activation operation for model: {}", activeProviderModel);
        requireNonNull(activeProviderModel, "Active status provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing provider activation transaction for model: {}", activeProviderModel);
            validate(activeProviderModel, isActiveProviderModelValid(errorList), errorList);

            final var optionalProvider = providerRepository.findById(activeProviderModel.getId());
            if (optionalProvider.isEmpty()) {
                throw new NotFoundException(String.format("Unable to update a provider with the id specified: %s", activeProviderModel.getId()));
            }

            ProviderModel result = toModel(providerRepository.save(toEntity(optionalProvider.get(), activeProviderModel)));

            logger.info("Provider activation operation completed for result: {}", activeProviderModel);
            return result;
        }, activeProviderModel);
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
