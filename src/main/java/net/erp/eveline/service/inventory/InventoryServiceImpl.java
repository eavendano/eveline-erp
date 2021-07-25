package net.erp.eveline.service.inventory;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.BadRequestException;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.data.entity.Inventory;
import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.data.repository.InventoryRepository;
import net.erp.eveline.data.repository.ProductRepository;
import net.erp.eveline.data.repository.WarehouseRepository;
import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.InventoryModel;
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
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.InventoryMapper.toModel;
import static net.erp.eveline.common.mapper.InventoryMapper.toEntity;
import static net.erp.eveline.common.mapper.InventoryMapper.toActiveModel;
import static net.erp.eveline.common.predicate.InventoryPredicates.INVENTORY_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.InventoryPredicates.isActiveInventoryModelValid;
import static net.erp.eveline.common.predicate.InventoryPredicates.isInventoryIdValid;
import static net.erp.eveline.common.predicate.InventoryPredicates.isInventoryModelValidForInsert;
import static net.erp.eveline.common.predicate.InventoryPredicates.isInventoryModelValidForUpdate;
import static net.erp.eveline.common.predicate.WarehousePredicates.WAREHOUSE_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseIdValid;

@Service
public class InventoryServiceImpl extends BaseService implements InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    private InventoryRepository inventoryRepository;
    private WarehouseRepository warehouseRepository;
    private ProductRepository productRepository;
    private TransactionService transactionService;

    @Override
    public Set<InventoryModel> findAll() {
        logger.info("Obtaining all inventory records.");
        return transactionService.performReadOnlyTransaction(status -> {
            logger.info("Requesting all inventory records.");
            Set<Inventory> inventory = Set.copyOf(inventoryRepository.findAll());
            logger.info("Retrieved all inventory records successfully.");
            return toModel(inventory);
        }, null);
    }

    @Override
    public Set<InventoryModel> findAllByWarehouse(String warehouseId) {
        logger.info("Requesting inventory for warehouse {}.", warehouseId);
        validate(warehouseId, isWarehouseIdValid(), WAREHOUSE_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            final Optional<Warehouse> optionalWarehouse = warehouseRepository.findById(warehouseId);
            Set<Inventory> inventories;
            if (optionalWarehouse.isPresent()) {
                inventories = inventoryRepository.findByWarehouseWarehouseId(warehouseId);
                logger.info("Retrieved {} inventory records for warehouse {} successfully.", inventories.size(), warehouseId);
                return toModel(inventories);
            }
            logger.info("Retrieved {} inventory records for warehouse {} successfully.", 0, warehouseId);
            return emptySet();
        }, warehouseId);
    }

    @Override
    public InventoryModel getInventoryModel(String inventoryId) {
        logger.info("Requesting inventory record matching id {}.", inventoryId);
        validate(inventoryId, isInventoryIdValid(), INVENTORY_ID_INVALID_MESSAGE);
        return transactionService.performReadOnlyTransaction(status -> {
            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new NotFoundException(format("Unable to find a inventory record with the id specified: %s", inventoryId)));

            logger.info("Retrieved {} inventory record for inventoryId {} successfully.", inventory, inventoryId);
            return toModel(inventory);
        }, inventoryId);
    }

    @Override
    public InventoryModel upsertInventoryModel(final InventoryModel inventoryModel) {
        logger.info("Upsert operation for model: {}", inventoryModel);
        requireNonNull(inventoryModel, "Model provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        final var inventoryId = ofNullable(inventoryModel.getId());
        if (inventoryId.isPresent()) {
            validate(inventoryModel, isInventoryModelValidForUpdate(errorList), errorList);
        } else {
            validate(inventoryModel, isInventoryModelValidForInsert(errorList), errorList);
        }

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing upsert transaction for model: {}", inventoryModel);
            InventoryModel result;

            final boolean warehouseExist = warehouseRepository.existsById(inventoryModel.getWarehouseModel().getId());
            final boolean productExist = productRepository.existsById(inventoryModel.getProductModel().getId());

            if (!warehouseExist) {
                String message = format("Unable to process operation since the warehouse doesn't exist: %s", inventoryModel);
                logger.info(message);
                throw new BadRequestException(message);
            }

            if (!productExist) {
                String message = format("Unable to process operation since the product doesn't exist: %s", inventoryModel);
                logger.info(message);
                throw new BadRequestException(message);
            }

            if (inventoryId.isPresent()) {
                // Try to perform the update

                final var optionalInventory = inventoryRepository.findById(inventoryId.get());
                if (optionalInventory.isEmpty()) {
                    throw new NotFoundException(format("Unable to update product with the id specified: %s", inventoryId));
                }

                // Definitely update the record on the DB.
                logger.info("Preparing to update inventory: {}", inventoryModel);
                result = toModel(inventoryRepository.save(toEntity(inventoryModel)));
                logger.info("Successful update operation for inventory: {}", inventoryModel);

            } else {
                // Try to perform insert if the rest of the values is valid
                logger.info("Preparing to insert inventory: {}", inventoryModel);
                result = toModel(inventoryRepository.save(toEntity(inventoryModel)));
                logger.info("Successful insert operation for inventory: {}", inventoryModel);
            }

            logger.info("Upsert operation completed for model: {}", inventoryModel);
            return result;
        }, inventoryModel);
    }

    @Override
    public ActiveInventoryModel activateInventory(ActiveInventoryModel activeInventoryModel) {
        logger.info("Activation operation for model: {}", activeInventoryModel);
        requireNonNull(activeInventoryModel, "Active status provided cannot be null or empty.");
        List<String> errorList = new ArrayList<>();
        validate(activeInventoryModel, isActiveInventoryModelValid(errorList), errorList);

        return transactionService.performWriteTransaction(status -> {
            logger.info("Performing inventory activation transaction for model: {}", activeInventoryModel);

            final Inventory inventory = inventoryRepository.findById(activeInventoryModel.getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Unable to update inventory with the id specified: %s", activeInventoryModel.getId())));

            ActiveInventoryModel result = toActiveModel(inventoryRepository.save(toEntity(inventory, activeInventoryModel)));

            logger.info("Product activation operation completed for result: {}", activeInventoryModel);
            return result;
        }, activeInventoryModel);
    }


    @Autowired
    public void setInventoryRepository(final InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired
    public void setWarehouseRepository(final WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Autowired
    public void setProductRepository(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setTransactionService(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
