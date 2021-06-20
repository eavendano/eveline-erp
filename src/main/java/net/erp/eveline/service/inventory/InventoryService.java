package net.erp.eveline.service.inventory;

import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.InventoryModel;

import java.util.Set;

public interface InventoryService {
    Set<InventoryModel> findAll();

    Set<InventoryModel> findAllByWarehouse(final String warehouseId);

    InventoryModel getInventoryModel(final String productId);

    InventoryModel upsertInventoryModel(final InventoryModel productModel);

    ActiveInventoryModel activateInventory(final ActiveInventoryModel activeInventoryModel);
}
