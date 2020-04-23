package net.erp.eveline.service.provider;

import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProviderRepository;
import net.erp.eveline.model.ProviderModel;
import net.erp.eveline.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.erp.eveline.common.mapper.ProviderMapper.toModel;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderIdValid;
import static net.erp.eveline.common.predicate.ProviderPredicates.providerIdInvalidMessage;

@Service
public class ProviderServiceImpl extends BaseService implements ProviderService {

    private ProviderRepository providerRepository;

    @Override
    public ProviderModel getProviderModel(final String providerId) {
        validate(providerId, isProviderIdValid(), providerIdInvalidMessage());
        final Optional<Provider> optionalProvider = providerRepository.findById(providerId);
        if (optionalProvider.isEmpty()) {
            throw new NotFoundException(String.format("Unable to find a provider with the id specified: %s", providerId));
        }
        return toModel(optionalProvider.get());
    }

    @Autowired
    public void setProviderRepository(final ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }
}
