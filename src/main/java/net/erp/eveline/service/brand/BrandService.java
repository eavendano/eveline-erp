package net.erp.eveline.service.brand;

import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.BrandModel;

import java.util.Set;

public interface BrandService {
    Set<BrandModel> findAll();

    BrandModel getBrandModel(final String brandId);

    BrandModel upsertBrandModel(final BrandModel brandModel);

    ActiveBrandModel activateBrand(final ActiveBrandModel activeBrandModel);

    Set<ActiveBrandModel> activateBrandSet(final Set<ActiveBrandModel> activeBrandModelSet);
}
