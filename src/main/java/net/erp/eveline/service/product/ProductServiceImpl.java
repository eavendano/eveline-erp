package net.erp.eveline.service.product;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.service.provider.ProviderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static net.erp.eveline.common.mapper.ProductMapper.toModel;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ProductRepository productRepository;
    private TransactionService transactionService;

    @Override
    public Set<ProductModel> findAllByProvider(String providerId) {
        logger.info("Requesting all products for provider {}.", providerId);
        return transactionService.performReadOnlyTransaction(status -> {

            Set< Product> products = productRepository.findAllByProviderProviderId(providerId);
            logger.info("Retrieved {} products for provider {} successfully.", products.size(), providerId);
            return toModel(products);
        }, null);
    }

    @Override
    public ProductModel getProductModel(String productId) {
        return null;
    }

    @Override
    public ProductModel findByUpc(String upc) {
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

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
