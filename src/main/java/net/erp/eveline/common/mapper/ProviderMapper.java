package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProviderModel;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProviderMapper {

    public static ProviderModel toModel(final Provider provider) {
        return new ProviderModel()
                .setId(provider.getProviderId())
                .setName(provider.getName())
                .setDescription(provider.getDescription())
                .setEmail(provider.getEmail())
                .setTelephone1(provider.getTelephone1())
                .setTelephone2(provider.getTelephone2())
                .setTelephone3(provider.getTelephone3())
                .setCreateDate(provider.getCreateDate())
                .setLastModified(provider.getLastModified())
                .setEnabled(provider.isEnabled())
                .setLastUser(provider.getLastUser());
    }

    public static ActiveProviderModel toActiveModel(final Provider provider) {
        return new ActiveProviderModel()
                .setId(provider.getProviderId())
                .setEnabled(provider.isEnabled())
                .setLastUser(provider.getLastUser());
    }

    public static Set<ProviderModel> toModel(final Set<Provider> providers) {
        return providers.stream()
                .map(ProviderMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static Provider toEntity(final ProviderModel providerModel) {
        final Provider entity = new Provider()
                .setProviderId(providerModel.getId())
                .setName(providerModel.getName())
                .setDescription(providerModel.getDescription())
                .setEmail(providerModel.getEmail())
                .setTelephone1(providerModel.getTelephone1())
                .setTelephone2(providerModel.getTelephone2())
                .setTelephone3(providerModel.getTelephone3())
                .setLastUser(providerModel.getLastUser());

        if (Optional.ofNullable(providerModel.isEnabled()).isPresent()) {
            entity.setEnabled(providerModel.isEnabled());
        }

        return entity;
    }

    public static Set<Provider> toEntity(final Set<ProviderModel> providerModelSet) {
        return providerModelSet.stream()
                .map(ProviderMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static Provider toEntity(final Provider provider, final ActiveProviderModel activeProviderModel) {
        return provider
                .setLastUser(activeProviderModel.getLastUser())
                .setEnabled(activeProviderModel.isEnabled());
    }
}
