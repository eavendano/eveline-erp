package net.erp.eveline.service.warehouse;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.common.mapper.WarehouseMapper;
import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.data.repository.WarehouseRepository;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.model.WarehouseModel;
import net.erp.eveline.model.WarehouseModel;
import net.erp.eveline.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static net.erp.eveline.common.mapper.WarehouseMapper.toEntity;
import static net.erp.eveline.common.mapper.WarehouseMapper.toModel;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseModelValidForInsert;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseModelValidForUpdate;
import static net.erp.eveline.common.predicate.WarehousePredicates.WAREHOUSE_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseIdValid;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class WarehouseServiceImpl extends BaseService implements WarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceImpl.class);
    private WarehouseRepository warehouseRepository;
    private TransactionService transactionService;

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
                // Try to perform insert if the rest of the values is valid
                logger.info("Preparing to insert warehouse: {}", warehouseModel);
                result = WarehouseMapper.toModel(warehouseRepository.save(toEntity(warehouseModel)));
                logger.info("Successful insert operation for warehouse: {}", warehouseModel);
            }

            logger.info("Upsert operation completed for model: {}", warehouseModel);
            return result;
        }, warehouseModel);
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
