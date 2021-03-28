package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Warehouse;
import net.erp.eveline.model.ActiveWarehouseModel;
import net.erp.eveline.model.WarehouseModel;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class WarehouseMapper {
    public static WarehouseModel toModel(final Warehouse warehouse) {
        return new WarehouseModel()
                .setId(warehouse.getWarehouseId())
                .setDescription(warehouse.getDescription())
                .setAddress1(warehouse.getAddress1())
                .setAddress2(warehouse.getAddress2())
                .setTelephone1(warehouse.getTelephone1())
                .setTelephone2(warehouse.getTelephone2())
                .setGeolocation(warehouse.getGeolocation())
                .setNotes(warehouse.getNotes())
                .setCreateDate(warehouse.getCreateDate())
                .setLastModified(warehouse.getLastModified())
                .setLastUser(warehouse.getLastUser())
                .setEnabled(warehouse.isEnabled());
    }

    public static Set<WarehouseModel> toModel(final Set<Warehouse> warehouses) {
        return warehouses
                .stream()
                .map(WarehouseMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static ActiveWarehouseModel toActiveModel(final Warehouse warehouse) {
        return new ActiveWarehouseModel()
                .setId(warehouse.getWarehouseId())
                .setEnabled(warehouse.isEnabled())
                .setLastUser(warehouse.getLastUser());
    }

    public static Set<ActiveWarehouseModel> toActiveModel(final Set<Warehouse> warehouseSet) {
        return warehouseSet.stream()
                .map(WarehouseMapper::toActiveModel)
                .collect(toSet());
    }

    public static Warehouse toEntity(final WarehouseModel warehouseModel) {
        final Warehouse entity = new Warehouse()
                .setDescription(warehouseModel.getDescription())
                .setAddress1(warehouseModel.getAddress1())
                .setAddress2(warehouseModel.getAddress2())
                .setTelephone1(warehouseModel.getTelephone1())
                .setTelephone2(warehouseModel.getTelephone2())
                .setGeolocation(warehouseModel.getGeolocation())
                .setNotes(warehouseModel.getNotes())
                .setCreateDate(warehouseModel.getCreateDate())
                .setLastModified(warehouseModel.getLastModified())
                .setLastUser(warehouseModel.getLastUser());

        if (Optional.ofNullable(warehouseModel.isEnabled()).isPresent()) {
            entity.setEnabled(warehouseModel.isEnabled());
        }
        return entity;
    }

    public static Set<Warehouse> toEntity(final Set<WarehouseModel> warehouseModelSet) {
        return warehouseModelSet
                .stream()
                .map(WarehouseMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static Warehouse toEntity(final Warehouse warehouse, final ActiveWarehouseModel activeactiveWarehouseModelModel) {
        return warehouse
                .setLastUser(activeactiveWarehouseModelModel.getLastUser())
                .setEnabled(activeactiveWarehouseModelModel.isEnabled());
    }

    public static Set<Warehouse> toEntity(final Set<Warehouse> warehouseSet, final Set<ActiveWarehouseModel> activeWarehouseModelSet) {
        return warehouseSet.stream()
                .map(warehouse -> {
                    final ActiveWarehouseModel activeWarehouseFound = activeWarehouseModelSet.stream()
                            .filter(activeWarehouseModel -> activeWarehouseModel.getId().equals(warehouse.getWarehouseId()))
                            .collect(toSet())
                            .iterator().next();
                    return toEntity(warehouse, activeWarehouseFound);
                }).collect(toSet());
    }
}
