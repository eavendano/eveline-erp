package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    boolean existsAllByWarehouseIdIn(final Set<String> providerIds);

}
