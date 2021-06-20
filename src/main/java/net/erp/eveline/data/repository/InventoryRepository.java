package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface InventoryRepository extends JpaRepository<Inventory, String> {

    Set<Inventory> findByWarehouseWarehouseId(final String warehouseId);
}
