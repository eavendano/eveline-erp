package net.erp.eveline.service.product;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.ProductMapper;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.service.BaseService;
import net.erp.eveline.service.provider.ProviderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.ProductMapper.toEntity;
import static net.erp.eveline.common.mapper.ProductMapper.toModel;
import static net.erp.eveline.common.predicate.ProductPredicates.isProductModelValidForInsert;
import static net.erp.eveline.common.predicate.ProductPredicates.isProductModelValidForUpdate;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ProductRepository productRepository;
    private TransactionService transactionService;

    @Override
    public Set<ProductModel> findAllByProvider(String providerId) {
        logger.info("Requesting all products for provider {}.", providerId);
        return transactionService.performReadOnlyTransaction(status -> {

            Set<Product> products = productRepository.findAllByProviderProviderId(providerId);
            logger.info("Retrieved {} products for provider {} successfully.", products.size(), providerId);
            return toModel(products);
        }, null);
    }

    @Override
    public ProductModel getProductModel(String productId) {
        logger.info("Requesting product matching id {}.", productId);
        return transactionService.performReadOnlyTransaction(status -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to find a product with the id specified: %s", productId)));

            logger.info("Retrieved {} product for productId {} successfully.", product, productId);
            return toModel(product);
        }, null);
    }

    @Override
    public ProductModel findByUpc(String upc) {
        logger.info("Requesting product matching upc {}.", upc);
        return transactionService.performReadOnlyTransaction(status -> {
            Product product = productRepository.findByUpc(upc)
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to find a product with the upc specified: %s", upc)));

            logger.info("Retrieved {} product for upc {} successfully.", product, upc);
            return toModel(product);
        }, null);
    }

    @Override
    public ProductModel upsertProductModel(final ProductModel productModel) {
        logger.info("Upsert operation for model: {}", productModel);
        requireNonNull(productModel, "Model provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing upsert transaction for model: {}", productModel);
            final var productId = ofNullable(productModel.getId());
            ProductModel result;

            if (productId.isPresent()) {
                // Try to perform the update
                validate(productModel, isProductModelValidForUpdate(errorList), errorList);

                final var optionalProduct = productRepository.findById(productId.get());
                if (optionalProduct.isEmpty()) {
                    throw new NotFoundException(String.format("Unable to update product with the id specified: %s", productId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update product: {}", productModel);
                result = ProductMapper.toModel(productRepository.save(toEntity(productModel)));
                logger.info("Successful update operation for product: {}", productModel);

            } else {
                // Try to perform insert if the rest of the values is valid
                validate(productModel, isProductModelValidForInsert(errorList), errorList);
                logger.info("Preparing to insert product: {}", productModel);
                result = ProductMapper.toModel(productRepository.save(toEntity(productModel)));
                logger.info("Successful insert operation for product: {}", productModel);
            }

            logger.info("Upsert operation completed for model: {}", productModel);
            return result;
        }, productModel);
    }

    @Override
    public ProductModel activateProduct(ActivateProductModel activateProductModel) {
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
