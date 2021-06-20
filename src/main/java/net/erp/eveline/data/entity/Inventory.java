package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "inventory")

public class Inventory {
    @Id
    @GeneratedValue(generator = "inventoryIdGenerator")
    @GenericGenerator(name = "inventoryIdGenerator", strategy = "net.erp.eveline.data.generators.CustomGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "prefix", value = "s"), @org.hibernate.annotations.Parameter(name = "sequence", value = "inventory_id_seq")})
    private String inventoryId;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="warehouse_id", nullable=false)
    private Warehouse warehouse;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "last_modified")
    private OffsetDateTime lastModified;

    @Column(name = "last_user")
    private String lastUser;

    @Column(name = "enabled")
    private Boolean enabled = true;

    public String getInventoryId() {
        return inventoryId;
    }

    public Inventory setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public Inventory setProduct(Product product) {
        this.product = product;
        return this;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Inventory setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Inventory setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public Inventory setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public Inventory setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Inventory setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Inventory setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Inventory inventory = (Inventory) o;

        return new EqualsBuilder()
                .append(inventoryId, inventory.inventoryId)
                .append(product, inventory.product)
                .append(warehouse, inventory.warehouse)
                .append(quantity, inventory.quantity)
                .append(createDate, inventory.createDate)
                .append(lastModified, inventory.lastModified)
                .append(lastUser, inventory.lastUser)
                .append(enabled, inventory.enabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(inventoryId)
                .append(product)
                .append(warehouse)
                .append(quantity)
                .append(createDate)
                .append(lastModified)
                .append(lastUser)
                .append(enabled)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("inventoryId", inventoryId)
                .append("product", product)
                .append("warehouse", warehouse)
                .append("quantity", quantity)
                .append("createDate", createDate)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .append("enabled", enabled)
                .toString();
    }
}
