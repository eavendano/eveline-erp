package net.erp.eveline.service.product;

import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.ProductModel;

import java.util.Set;

public interface ProductService {
    Set<ProductModel> findAllByProvider(final String providerId);

    Set<ProductModel> findAll();

    ProductModel getProductModel(final String productId);

    ProductModel upsertProductModel(final ProductModel productModel);

    ActiveProductModel activateProduct(final ActiveProductModel activeProductModel);

    ProductModel findByUpc(final String upc);
}
