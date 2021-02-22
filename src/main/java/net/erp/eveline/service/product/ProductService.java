package net.erp.eveline.service.product;

import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ProductModel;

import java.util.Set;

public interface ProductService {
    Set<ProductModel> findAllByProvider(final String providerId);

    ProductModel getProductModel(final String productId);

    ProductModel upsertProductModel(final ProductModel productModel);

    ProductModel activateProduct(final ActivateProductModel activateProductModel);

    ProductModel findByUpc(final String upc);
}
