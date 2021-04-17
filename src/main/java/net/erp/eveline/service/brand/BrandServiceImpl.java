package net.erp.eveline.service.brand;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.BrandMapper;
import net.erp.eveline.data.entity.Brand;
import net.erp.eveline.data.repository.BrandRepository;
import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.BrandModel;
import net.erp.eveline.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static net.erp.eveline.common.mapper.BrandMapper.*;
import static net.erp.eveline.common.predicate.BrandPredicates.*;
import static net.erp.eveline.common.predicate.BrandPredicates.isActiveBrandSetValid;

@Service
public class BrandServiceImpl  extends BaseService implements BrandService {
    private static final Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);
    private BrandRepository brandRepository;
    private TransactionService transactionService;

    @Override
    public Set<BrandModel> findAll() {
        logger.info("Obtaining all brands.");
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Requesting all brands.");
            Set<Brand> brands = Set.copyOf(brandRepository.findAll());
            logger.info("Retrieved all brands successfully.");
            return toModel(brands);
        }, null);
    }


    @Override
    public BrandModel getBrandModel(String brandId) {
        logger.info("Requesting brand matching id {}.", brandId);
        validate(brandId, isBrandIdValid(), BRAND_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new NotFoundException(format("Unable to find a brand with the id specified: %s", brandId)));

            logger.info("Retrieved {} brand for brandId {} successfully.", brand, brandId);
            return toModel(brand);
        }, brandId);
    }

    @Override
    public BrandModel upsertBrandModel(BrandModel brandModel) {
        logger.info("Upsert operation for model: {}", brandModel);
        requireNonNull(brandModel, "Model provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        final var brandId = ofNullable(brandModel.getId());
        if (brandId.isPresent()) {
            validate(brandModel, isBrandModelValidForUpdate(errorList), errorList);
        } else {
            validate(brandModel, isBrandModelValidForInsert(errorList), errorList);
        }
        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing upsert transaction for model: {}", brandModel);
            BrandModel result;
            if (brandId.isPresent()) {
                validate(brandModel, isBrandModelValidForUpdate(errorList), errorList);
                // Try to perform the update
                final var brandExists = brandRepository.existsById(brandId.get());
                if (!brandExists) {
                    throw new NotFoundException(String.format("Unable to update a brand with the id specified: %s", brandId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update brand: {}", brandModel);
                result = BrandMapper.toModel(brandRepository.save(toEntity(brandModel)));
                logger.info("Successful update operation for brand: {}", brandModel);
            } else {
                validate(brandModel, isBrandModelValidForInsert(errorList), errorList);
                // Try to perform insert if the rest of the values is valid
                logger.info("Preparing to insert brand: {}", brandModel);
                result = BrandMapper.toModel(brandRepository.save(toEntity(brandModel)));
                logger.info("Successful insert operation for brand: {}", brandModel);
            }
            logger.info("Upsert operation completed for model: {}", brandModel);
            return result;
        }, brandModel);
    }

    @Override
    public ActiveBrandModel activateBrand(final ActiveBrandModel activeBrandModel) {
        logger.info("Activation operation for model: {}", activeBrandModel);
        requireNonNull(activeBrandModel, "Active status brand cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        validate(activeBrandModel, isActiveBrandModelValid(errorList), errorList);

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing brand activation transaction for model: {}", activeBrandModel);
            final var optionalBrand = brandRepository.findById(activeBrandModel.getId());
            if (optionalBrand.isEmpty()) {
                throw new NotFoundException(String.format("Unable to update a brand with the id specified: %s", activeBrandModel.getId()));
            }

            var result = toActiveModel(brandRepository.save(toEntity(optionalBrand.get(), activeBrandModel)));

            logger.info("Brand activation operation completed for result: {}", activeBrandModel);
            return result;
        }, activeBrandModel);
    }

    @Override
    public Set<ActiveBrandModel> activateBrandSet(final Set<ActiveBrandModel> activeBrandModelSet) {
        logger.info("Activation operation for set of models: {}", activeBrandModelSet);
        requireNonNull(activeBrandModelSet, "Active status set provided cannot be null or empty.");
        if (activeBrandModelSet.isEmpty()) {
            return emptySet();
        }
        List<String> errorList = new ArrayList<>();
        validate(activeBrandModelSet, isActiveBrandSetValid(errorList), errorList);

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing brand activation transaction for set of models: {}", activeBrandModelSet);
            final var brandIds = activeBrandModelSet.stream().map(ActiveBrandModel::getId).collect(toSet());
            final Boolean existsAllById = brandRepository.existsAllByBrandIdIn(brandIds);
            if (!existsAllById) {
                throw new NotFoundException(String.format("Unable to update brands with the ids specified: %s", brandIds));
            }

            final var brandSet = Set.copyOf(brandRepository.findAllById(brandIds));
            if (brandSet.isEmpty()) {
                throw new NotFoundException(String.format("Unable to update all brands with the ids specified since none where found: %s", brandIds));
            }

            var result = toActiveModel(Set.copyOf(brandRepository.saveAll(BrandMapper.toEntity(brandSet, activeBrandModelSet))));

            logger.info("Brand activation operation completed for results: {}", activeBrandModelSet);
            return result;
        }, activeBrandModelSet);
    }


    @Autowired
    public void setBrandRepository(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
