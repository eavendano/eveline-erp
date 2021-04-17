package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Brand;
import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.BrandModel;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class BrandMapper {
    public static BrandModel toModel(final Brand brand) {
        return new BrandModel()
                .setId(brand.getBrandId())
                .setName(brand.getName())
                .setDescription(brand.getDescription())
                .setCreateDate(brand.getCreateDate())
                .setLastModified(brand.getLastModified())
                .setLastUser(brand.getLastUser())
                .setEnabled(brand.getEnabled());
    }

    public static Set<BrandModel> toModel(final Set<Brand> brands) {
        return brands
                .stream()
                .map(BrandMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static ActiveBrandModel toActiveModel(final Brand brand) {
        return new ActiveBrandModel()
                .setId(brand.getBrandId())
                .setEnabled(brand.getEnabled())
                .setLastUser(brand.getLastUser());
    }

    public static Set<ActiveBrandModel> toActiveModel(final Set<Brand> brandSet) {
        return brandSet.stream()
                .map(BrandMapper::toActiveModel)
                .collect(toSet());
    }

    public static Brand toEntity(final BrandModel brandModel) {
        final Brand entity = new Brand()
                .setBrandId(brandModel.getId())
                .setDescription(brandModel.getDescription())
                .setName(brandModel.getName())
                .setCreateDate(brandModel.getCreateDate())
                .setLastModified(brandModel.getLastModified())
                .setLastUser(brandModel.getLastUser());

        if (Optional.ofNullable(brandModel.getEnabled()).isPresent()) {
            entity.setEnabled(brandModel.getEnabled());
        }
        return entity;
    }

    public static Brand toEntity(final Brand brand, final ActiveBrandModel activeBrandModel) {
        return brand
                .setLastUser(activeBrandModel.getLastUser())
                .setEnabled(activeBrandModel.isEnabled());
    }

    public static Set<Brand> toEntity(final Set<Brand> brandSet, final Set<ActiveBrandModel> activeBrandModelSet) {
        return brandSet.stream()
                .map(brand -> {
                    final ActiveBrandModel activeBrandFound = activeBrandModelSet.stream()
                            .filter(activeBrandModel -> activeBrandModel.getId().equals(brand.getBrandId()))
                            .collect(toSet())
                            .iterator().next();
                    return toEntity(brand, activeBrandFound);
                }).collect(toSet());
    }
}
