package net.erp.eveline.service.provider;

import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProviderModel;

import java.util.Set;

public interface ProviderService {

    Set<ProviderModel> findAll();

    ProviderModel getProviderModel(final String providerId);

    ProviderModel upsertProviderModel(final ProviderModel providerModel);

    ActiveProviderModel activateProvider(final ActiveProviderModel activeProviderModel);
}
