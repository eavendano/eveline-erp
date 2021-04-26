package net.erp.eveline.service.warehouse;

import net.erp.eveline.model.ActiveWarehouseModel;
import net.erp.eveline.model.WarehouseModel;

import java.util.Set;

public interface WarehouseService {
    Set<WarehouseModel> findAll();

    WarehouseModel getWarehouseModel(final String warehouseId);

    WarehouseModel upsertWarehouseModel(final WarehouseModel warehouseModel);

    ActiveWarehouseModel activateWarehouse(final ActiveWarehouseModel activeWarehouseModel);

    Set<ActiveWarehouseModel> activateWarehouseSet(final Set<ActiveWarehouseModel> activeWarehouseModelSet);
}
