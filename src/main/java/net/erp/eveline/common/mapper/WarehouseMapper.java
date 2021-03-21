package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.model.WarehouseModel;

import java.util.Set;
import java.util.stream.Collectors;

public class WarehouseMapper {
    public static WarehouseModel toModel(final Warehouse warehouse) {
        return new WarehouseModel()
                .setId(warehouse.getWarehouseId())
                .setDescription(warehouse.getDescription())
                .setGeolocation(warehouse.getGeolocation())
                .setCreateDate(warehouse.getCreateDate())
                .setLastModified(warehouse.getLastModified())
                .setLastUser(warehouse.getLastUser());
    }

    public static Set<WarehouseModel> toModel(final Set<Warehouse> warehouses) {
        return warehouses
                .stream()
                .map(WarehouseMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static Warehouse toEntity(final WarehouseModel warehouseModel) {
        return new Warehouse()
                .setWarehouseId(warehouseModel.getId())
                .setDescription(warehouseModel.getDescription())
                .setGeolocation(warehouseModel.getGeolocation())
                .setLastUser(warehouseModel.getLastUser());
    }

    public static Set<Warehouse> toEntity(final Set<WarehouseModel> warehouseModels) {
        return warehouseModels
                .stream()
                .map(WarehouseMapper::toEntity)
                .collect(Collectors.toSet());
    }
}
