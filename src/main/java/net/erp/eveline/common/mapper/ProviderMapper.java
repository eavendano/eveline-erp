package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.model.ProviderModel;

public class ProviderMapper {

    public static ProviderModel toModel(final Provider provider) {
        return new ProviderModel()
                .setId(provider.getProviderId())
                .setDescription(provider.getDescription())
                .setEmail(provider.getEmail())
                .setTelephone1(provider.getTelephone1())
                .setTelephone2(provider.getTelephone2())
                .setTelephone3(provider.getTelephone3())
                .setCreateDate(provider.getCreateDate())
                .setLastModified(provider.getLastModified())
                .setLastUser(provider.getLastUser());
    }
}
