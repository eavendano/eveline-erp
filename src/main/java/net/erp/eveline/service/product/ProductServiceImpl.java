package net.erp.eveline.service.product;

import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Set<ProductModel> findAllByProvider(String providerId) {
        return null;
    }

    @Override
    public ProductModel getProductModel(String productId) {
        return null;
    }

    @Override
    public ProductModel upsertProductModel(ProductModel productModel) {
        return null;
    }

    @Override
    public ProductModel activateProvider(ActivateProductModel activateProductModel) {
        return null;
    }
}
