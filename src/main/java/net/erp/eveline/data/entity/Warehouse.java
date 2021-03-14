package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(generator = "warehouseIdGenerator")
    @GenericGenerator(name = "warehouseIdGenerator", strategy = "net.erp.eveline.data.generators.CustomGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "prefix", value = "s"), @org.hibernate.annotations.Parameter(name = "sequence", value = "warehouse_id_seq")})
    private String warehouseId;

    @Column(name = "description")
    private String description;

    @Column(name="location")
    private String location; //TODO is this the address?

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "last_modified")
    private OffsetDateTime lastModified;

    @Column(name = "last_user")
    private String lastUser;


    public String getWarehouseId() {
        return warehouseId;
    }

    public Warehouse setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Warehouse setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Warehouse setLocation(String location) {
        this.location = location;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public Warehouse setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public Warehouse setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Warehouse setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Warehouse warehouse = (Warehouse) o;

        return new EqualsBuilder()
                .append(warehouseId, warehouse.warehouseId)
                .append(description, warehouse.description)
                .append(location, warehouse.location)
                .isEquals();
    }
}
