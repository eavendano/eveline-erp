package net.erp.eveline.service.product;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.ProductMapper;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.data.repository.ProviderRepository;
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

import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.ProductMapper.toEntity;
import static net.erp.eveline.common.mapper.ProductMapper.toModel;
import static net.erp.eveline.common.predicate.ProductPredicates.*;
import static net.erp.eveline.common.predicate.ProviderPredicates.PROVIDER_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderIdValid;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ProductRepository productRepository;
    private ProviderRepository providerRepository;
    private TransactionService transactionService;

    @Override
    public Set<ProductModel> findAllByProvider(final String providerId) {
        logger.info("Requesting all products for provider {}.", providerId);
        validate(providerId, isProviderIdValid(), PROVIDER_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            final Optional<Provider> optionalProvider = providerRepository.findById(providerId);
            Set<Product> products = null;
            if (optionalProvider.isPresent()) {
                products = productRepository.findByProviderSetProviderId(providerId);
                logger.info("Retrieved {} products for provider {} successfully.", products.size(), providerId);
                return toModel(products);
            }
            logger.info("Retrieved {} products for provider {} successfully.", 0, providerId);
            return emptySet();
        }, providerId);
    }

    @Override
    public ProductModel getProductModel(final String productId) {
        logger.info("Requesting product matching id {}.", productId);
        validate(productId, isProductIdValid(), PRODUCT_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to find a product with the id specified: %s", productId)));

            logger.info("Retrieved {} product for productId {} successfully.", product, productId);
            return toModel(product);
        }, productId);
    }

    @Override
    public ProductModel findByUpc(final String upc) {
        logger.info("Requesting product matching upc {}.", upc);
        validate(upc, isProductUpcValid(), PRODUCT_UPC_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            Product product = productRepository.findByUpc(upc)
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to find a product with the upc specified: %s", upc)));

            logger.info("Retrieved {} product for upc {} successfully.", product, upc);
            return toModel(product);
        }, upc);
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

            final boolean allProvidersExist = productModel.getProviderSet()
                    .stream()
                    .allMatch(providerId -> providerRepository.existsById(providerId));

            if (!allProvidersExist) {
                String message = String.format("Unable to process operation since not all providers are valid or exist for product: %s", productModel);
                logger.info(message);
                throw new BadRequestException(message);
            }

            final var providerEntitySet = Set.copyOf(providerRepository.findAllById(productModel.getProviderSet()));

            if (productId.isPresent()) {
                // Try to perform the update
                validate(productModel, isProductModelValidForUpdate(errorList), errorList);

                final var optionalProduct = productRepository.findById(productId.get());
                if (optionalProduct.isEmpty()) {
                    throw new NotFoundException(String.format("Unable to update product with the id specified: %s", productId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update product: {}", productModel);
                result = ProductMapper.toModel(productRepository.save(toEntity(productModel, providerEntitySet)));
                logger.info("Successful update operation for product: {}", productModel);

            } else {
                // Try to perform insert if the rest of the values is valid
                validate(productModel, isProductModelValidForInsert(errorList), errorList);
                logger.info("Preparing to insert product: {}", productModel);
                result = ProductMapper.toModel(productRepository.save(toEntity(productModel, providerEntitySet)));
                logger.info("Successful insert operation for product: {}", productModel);
            }

            logger.info("Upsert operation completed for model: {}", productModel);
            return result;
        }, productModel);
    }

    @Override
    public ProductModel activateProduct(final ActivateProductModel activateProductModel) {
        logger.info("Activation operation for model: {}", activateProductModel);
        requireNonNull(activateProductModel, "Active status provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing product activation transaction for model: {}", activateProductModel);
            validate(activateProductModel, isActiveProductModelValid(errorList), errorList);

            final var product = productRepository.findById(activateProductModel.getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to update a provider with the id specified: %s", activateProductModel.getId())));

            ProductModel result = ProductMapper.toModel(productRepository.save(ProductMapper.toEntity(product, activateProductModel)));

            logger.info("Product activation operation completed for result: {}", activateProductModel);
            return result;
        }, activateProductModel);
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProviderRepository(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
