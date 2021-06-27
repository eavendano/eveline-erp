package net.erp.eveline.service.product;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.ProductMapper;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.data.repository.ProviderRepository;
import net.erp.eveline.model.ActiveProductModel;
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
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.ProductMapper.toActiveModel;
import static net.erp.eveline.common.mapper.ProductMapper.toEntity;
import static net.erp.eveline.common.mapper.ProductMapper.toModel;
import static net.erp.eveline.common.predicate.ProductPredicates.PRODUCT_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.ProductPredicates.PRODUCT_UPC_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.ProductPredicates.isActiveProductModelValid;
import static net.erp.eveline.common.predicate.ProductPredicates.isProductIdValid;
import static net.erp.eveline.common.predicate.ProductPredicates.isProductModelValidForInsert;
import static net.erp.eveline.common.predicate.ProductPredicates.isProductModelValidForUpdate;
import static net.erp.eveline.common.predicate.ProductPredicates.isProductUpcValid;
import static net.erp.eveline.common.predicate.ProviderPredicates.PROVIDER_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.ProviderPredicates.isProviderIdValid;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private ProductRepository productRepository;
    private ProviderRepository providerRepository;
    private TransactionService transactionService;

    @Override
    public Set<ProductModel> findAll() {
        logger.info("Obtaining all products.");
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Requesting all products.");
            Set<Product> products = Set.copyOf(productRepository.findAll());
            logger.info("Retrieved all products successfully.");
            return ProductMapper.toModel(products);
        }, null);
    }


    @Override
    public Set<ProductModel> findAllByProvider(final String providerId) {
        logger.info("Requesting all products for provider {}.", providerId);
        validate(providerId, isProviderIdValid(), PROVIDER_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            final Optional<Provider> optionalProvider = providerRepository.findById(providerId);
            Set<Product> products;
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
                    .orElseThrow(() -> new NotFoundException(format("Unable to find a product with the id specified: %s", productId)));

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
                    .orElseThrow(() -> new NotFoundException(format("Unable to find a product with the upc specified: %s", upc)));

            logger.info("Retrieved {} product for upc {} successfully.", product, upc);
            return toModel(product);
        }, upc);
    }

    @Override
    public ProductModel upsertProductModel(final ProductModel productModel) {
        logger.info("Upsert operation for model: {}", productModel);
        requireNonNull(productModel, "Model provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        final var productId = ofNullable(productModel.getId());
        if (productId.isPresent()) {
            validate(productModel, isProductModelValidForUpdate(errorList), errorList);
        } else {
            validate(productModel, isProductModelValidForInsert(errorList), errorList);
        }

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing upsert transaction for model: {}", productModel);
            ProductModel result;

            final boolean allProvidersExist = productModel.getProviderSet()
                    .stream()
                    .allMatch(provider -> providerRepository.existsById(provider.getId()));

            if (!allProvidersExist) {
                String message = format("Unable to process operation since not all providers are valid or exist for product: %s", productModel);
                logger.info(message);
                throw new BadRequestException(message);
            }

            final var providerIds = productModel.getProviderSet().stream().map((providerModel -> providerModel.getId()));
            final var providerEntitySet = Set.copyOf(providerRepository.findAllById(providerIds.collect(Collectors.toSet())));

            if (productId.isPresent()) {
                // Try to perform the update

                final var optionalProduct = productRepository.findById(productId.get());
                if (optionalProduct.isEmpty()) {
                    throw new NotFoundException(format("Unable to update product with the id specified: %s", productId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update product: {}", productModel);
                result = ProductMapper.toModel(productRepository.save(toEntity(productModel, providerEntitySet)));
                logger.info("Successful update operation for product: {}", productModel);

            } else {
                // Try to perform insert if the rest of the values is valid
                logger.info("Preparing to insert product: {}", productModel);
                result = ProductMapper.toModel(productRepository.save(toEntity(productModel, providerEntitySet)));
                logger.info("Successful insert operation for product: {}", productModel);
            }

            logger.info("Upsert operation completed for model: {}", productModel);
            return result;
        }, productModel);
    }

    @Override
    public ActiveProductModel activateProduct(final ActiveProductModel activeProductModel) {
        logger.info("Activation operation for model: {}", activeProductModel);
        requireNonNull(activeProductModel, "Active status provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        validate(activeProductModel, isActiveProductModelValid(errorList), errorList);

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing product activation transaction for model: {}", activeProductModel);

            final Product product = productRepository.findById(activeProductModel.getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to update a provider with the id specified: %s", activeProductModel.getId())));

            var result = toActiveModel(productRepository.save(ProductMapper.toEntity(product, activeProductModel)));

            logger.info("Product activation operation completed for result: {}", activeProductModel);
            return result;
        }, activeProductModel);
    }

    @Autowired
    public void setProductRepository(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProviderRepository(final ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Autowired
    public void setTransactionService(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
