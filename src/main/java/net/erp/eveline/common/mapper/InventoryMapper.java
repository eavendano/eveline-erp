package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Inventory;
import net.erp.eveline.data.entity.Product;
import net.erp.eveline.model.ActiveInventoryModel;
import net.erp.eveline.model.InventoryModel;
import net.erp.eveline.model.ProductModel;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InventoryMapper {
    public static InventoryModel toModel(final Inventory inventory) {
        return new InventoryModel()
                .setId(inventory.getInventoryId())
                .setProductModel(ProductMapper.toModel(inventory.getProduct()))
                .setWarehouseModel(WarehouseMapper.toModel(inventory.getWarehouse()))
                .setQuantity(inventory.getQuantity())
                .setCreateDate(inventory.getCreateDate())
                .setLastModified(inventory.getLastModified())
                .setEnabled(inventory.getEnabled())
                .setLastUser(inventory.getLastUser());
    }

    public static Set<InventoryModel> toModel(final Set<Inventory> inventories) {
        return inventories.stream()
                .map(InventoryMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static ActiveInventoryModel toActiveModel(final Inventory product) {
        return new ActiveInventoryModel()
                .setId(product.getInventoryId())
                .setEnabled(product.getEnabled())
                .setLastUser(product.getLastUser());
    }

    public static Inventory toEntity(final InventoryModel inventoryModel) {
        final Inventory entity = new Inventory()
                .setInventoryId(inventoryModel.getId())
                .setProduct(ProductMapper.toEntity(inventoryModel.getProductModel()))
                .setWarehouse(WarehouseMapper.toEntity(inventoryModel.getWarehouseModel()))
                .setQuantity(inventoryModel.getQuantity())
                .setCreateDate(inventoryModel.getCreateDate())
                .setLastModified(inventoryModel.getLastModified())
                .setLastUser(inventoryModel.getLastUser());

        if (Optional.ofNullable(inventoryModel.getEnabled()).isPresent()) {
            entity.setEnabled(inventoryModel.getEnabled());
        }
        return entity;
    }

    public static Product toEntity(final ProductModel productModel) {
        final Product entity = new Product()
                .setProductId(productModel.getId())
                .setUpc(productModel.getUpc())
                .setTitle(productModel.getTitle())
                .setDescription(productModel.getDescription())
                .setCreateDate(productModel.getCreateDate())
                .setLastModified(productModel.getLastModified())
                .setEnabled(productModel.isEnabled())
                .setLastUser(productModel.getLastUser());

        if (Optional.ofNullable(productModel.isEnabled()).isPresent()) {
            entity.setEnabled(productModel.isEnabled());
        }
        return entity;
    }

    public static Set<Inventory> toEntity(final Set<InventoryModel> inventoryModelSet) {
        return inventoryModelSet
                .stream()
                .map(InventoryMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static Inventory toEntity(final Inventory inventory, final ActiveInventoryModel activeInventoryModel) {
        return inventory
                .setLastUser(activeInventoryModel.getLastUser())
                .setEnabled(activeInventoryModel.isEnabled());
    }

}
