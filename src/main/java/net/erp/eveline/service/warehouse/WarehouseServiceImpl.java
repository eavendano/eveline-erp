package net.erp.eveline.service.warehouse;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.WarehouseMapper;
import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.data.repository.WarehouseRepository;
import net.erp.eveline.model.ActiveWarehouseModel;
import net.erp.eveline.model.WarehouseModel;
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
import static net.erp.eveline.common.mapper.WarehouseMapper.toActiveModel;
import static net.erp.eveline.common.mapper.WarehouseMapper.toEntity;
import static net.erp.eveline.common.mapper.WarehouseMapper.toModel;
import static net.erp.eveline.common.predicate.WarehousePredicates.WAREHOUSE_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseIdValid;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseModelValidForUpdate;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseModelValidForInsert;
import static net.erp.eveline.common.predicate.WarehousePredicates.isActiveWarehouseModelValid;
import static net.erp.eveline.common.predicate.WarehousePredicates.isActiveWarehouseSetValid;

@Service
public class WarehouseServiceImpl extends BaseService implements WarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);
    private WarehouseRepository warehouseRepository;
    private TransactionService transactionService;

    @Override
    public Set<WarehouseModel> findAll() {
        logger.info("Obtaining all warehouses.");
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Requesting all warehouses.");
            Set<Warehouse> warehouses = Set.copyOf(warehouseRepository.findAll());
            logger.info("Retrieved all warehouses successfully.");
            return toModel(warehouses);
        }, null);
    }


    @Override
    public WarehouseModel getWarehouseModel(String warehouseId) {
        logger.info("Requesting warehouse matching id {}.", warehouseId);
        validate(warehouseId, isWarehouseIdValid(), WAREHOUSE_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            Warehouse warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new NotFoundException(format("Unable to find a warehouse with the id specified: %s", warehouseId)));

            logger.info("Retrieved {} warehouse for warehouseId {} successfully.", warehouse, warehouseId);
            return toModel(warehouse);
        }, warehouseId);
    }

    @Override
    public WarehouseModel upsertWarehouseModel(WarehouseModel warehouseModel) {
        logger.info("Upsert operation for model: {}", warehouseModel);
        requireNonNull(warehouseModel, "Model provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        final var warehouseId = ofNullable(warehouseModel.getId());
        if (warehouseId.isPresent()) {
            validate(warehouseModel, isWarehouseModelValidForUpdate(errorList), errorList);
        } else {
            validate(warehouseModel, isWarehouseModelValidForInsert(errorList), errorList);
        }
        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing upsert transaction for model: {}", warehouseModel);
            WarehouseModel result;
            if (warehouseId.isPresent()) {
                validate(warehouseModel, isWarehouseModelValidForUpdate(errorList), errorList);
                // Try to perform the update
                final var warehouseExists = warehouseRepository.existsById(warehouseId.get());
                if (!warehouseExists) {
                    throw new NotFoundException(String.format("Unable to update a warehouse with the id specified: %s", warehouseId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update warehouse: {}", warehouseModel);
                result = WarehouseMapper.toModel(warehouseRepository.save(toEntity(warehouseModel)));
                logger.info("Successful update operation for warehouse: {}", warehouseModel);
            } else {
                validate(warehouseModel, isWarehouseModelValidForInsert(errorList), errorList);
                // Try to perform insert if the rest of the values is valid
                logger.info("Preparing to insert warehouse: {}", warehouseModel);
                result = WarehouseMapper.toModel(warehouseRepository.save(toEntity(warehouseModel)));
                logger.info("Successful insert operation for warehouse: {}", warehouseModel);
            }
            logger.info("Upsert operation completed for model: {}", warehouseModel);
            return result;
        }, warehouseModel);
    }

    @Override
    public ActiveWarehouseModel activateWarehouse(final ActiveWarehouseModel activeWarehouseModel) {
        logger.info("Activation operation for model: {}", activeWarehouseModel);
        requireNonNull(activeWarehouseModel, "Active status warehouse cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        validate(activeWarehouseModel, isActiveWarehouseModelValid(errorList), errorList);

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing warehouse activation transaction for model: {}", activeWarehouseModel);
            final var optionalWarehouse = warehouseRepository.findById(activeWarehouseModel.getId());
            if (optionalWarehouse.isEmpty()) {
                throw new NotFoundException(String.format("Unable to update a warehouse with the id specified: %s", activeWarehouseModel.getId()));
            }

            var result = toActiveModel(warehouseRepository.save(toEntity(optionalWarehouse.get(), activeWarehouseModel)));

            logger.info("Warehouse activation operation completed for result: {}", activeWarehouseModel);
            return result;
        }, activeWarehouseModel);
    }

    @Override
    public Set<ActiveWarehouseModel> activateWarehouseSet(final Set<ActiveWarehouseModel> activeWarehouseModelSet) {
        logger.info("Activation operation for set of models: {}", activeWarehouseModelSet);
        requireNonNull(activeWarehouseModelSet, "Active status set provided cannot be null or empty.");
        if (activeWarehouseModelSet.isEmpty()) {
            return emptySet();
        }
        List<String> errorList = new ArrayList<>();
        validate(activeWarehouseModelSet, isActiveWarehouseSetValid(errorList), errorList);

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing warehouse activation transaction for set of models: {}", activeWarehouseModelSet);
            final var warehouseIds = activeWarehouseModelSet.stream().map(ActiveWarehouseModel::getId).collect(toSet());
            final Boolean existsAllById = warehouseRepository.existsAllByWarehouseIdIn(warehouseIds);
            if (!existsAllById) {
                throw new NotFoundException(String.format("Unable to update warehouses with the ids specified: %s", warehouseIds));
            }

            final var warehouseSet = Set.copyOf(warehouseRepository.findAllById(warehouseIds));
            if (warehouseSet.isEmpty()) {
                throw new NotFoundException(String.format("Unable to update all warehouses with the ids specified since none where found: %s", warehouseIds));
            }

            var result = toActiveModel(Set.copyOf(warehouseRepository.saveAll(WarehouseMapper.toEntity(warehouseSet, activeWarehouseModelSet))));

            logger.info("Warehouse activation operation completed for results: {}", activeWarehouseModelSet);
            return result;
        }, activeWarehouseModelSet);
    }


    @Autowired
    public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
