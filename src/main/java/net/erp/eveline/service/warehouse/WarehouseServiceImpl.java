package net.erp.eveline.service.warehouse;

import net.erp.eveline.common.TransactionService;
import net.erp.eveline.common.exception.NotFoundException;
import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.data.repository.WarehouseRepository;
import net.erp.eveline.model.WarehouseModel;
import net.erp.eveline.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static net.erp.eveline.common.mapper.WarehouseMapper.toModel;
import static net.erp.eveline.common.predicate.WarehousePredicates.WAREHOUSE_ID_INVALID_MESSAGE;
import static net.erp.eveline.common.predicate.WarehousePredicates.isWarehouseIdValid;

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

    @Autowired
    public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
