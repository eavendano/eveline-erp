package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class InventoryModel {
    private String id;
    private ProductModel productModel;
    private WarehouseModel warehouseModel;
    private Integer quantity;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public InventoryModel setId(String id) {
        this.id = id;
        return this;
    }

    public ProductModel getProductModel() {
        return productModel;
    }

    public InventoryModel setProductModel(ProductModel productModel) {
        this.productModel = productModel;
        return this;
    }

    public WarehouseModel getWarehouseModel() {
        return warehouseModel;
    }

    public InventoryModel setWarehouseModel(WarehouseModel warehouseModel) {
        this.warehouseModel = warehouseModel;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public InventoryModel setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public InventoryModel setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public InventoryModel setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public InventoryModel setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public InventoryModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InventoryModel that = (InventoryModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(productModel, that.productModel)
                .append(warehouseModel, that.warehouseModel)
                .append(quantity, that.quantity)
                .append(createDate, that.createDate)
                .append(lastModified, that.lastModified)
                .append(lastUser, that.lastUser)
                .append(enabled, that.enabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("productModel", productModel.toString())
                .append("warehouseModel", warehouseModel.toString())
                .append("quantity", quantity)
                .append("createDate", createDate)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .append("enabled", enabled)
                .toString();
    }
}
