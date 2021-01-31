package net.erp.eveline.service.provider;

import net.erp.eveline.model.ProviderModel;

import java.util.List;

public interface ProviderService {

    ProviderModel getProviderModel(final String providerId);

    List<ProviderModel> getProviderModels();
}
