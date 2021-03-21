package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(generator = "warehouseIdGenerator")
    @GenericGenerator(name = "warehouseIdGenerator", strategy = "net.erp.eveline.data.generators.CustomGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "prefix", value = "s"), @org.hibernate.annotations.Parameter(name = "sequence", value = "warehouse_id_seq")})
    private String warehouseId;


    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "telephone1")
    private String telephone1;

    @Column(name = "telephone2")
    private String telephone2;

    @Column(name = "telephone3")
    private String telephone3;

    @Column(name = "geolocation")
    private String geolocation;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "enabled")
    private Boolean enabled = true;

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

    public String getName() {
        return name;
    }

    public Warehouse setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Warehouse setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public Warehouse setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public Warehouse setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public Warehouse setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
        return this;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public Warehouse setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
        return this;
    }

    public String getTelephone3() {
        return telephone3;
    }

    public Warehouse setTelephone3(String telephone3) {
        this.telephone3 = telephone3;
        return this;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public Warehouse setGeolocation(String geolocation) {
        this.geolocation = geolocation;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Warehouse setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
                .append(name, warehouse.name)
                .append(description, warehouse.description)
                .append(address1, warehouse.address1)
                .append(address2, warehouse.address2)
                .append(telephone1, warehouse.telephone1)
                .append(telephone2, warehouse.telephone2)
                .append(telephone3, warehouse.telephone3)
                .append(geolocation, warehouse.geolocation)
                .append(createDate, warehouse.createDate)
                .append(enabled, warehouse.enabled)
                .append(lastModified, warehouse.lastModified)
                .append(lastUser, warehouse.lastUser)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("warehouseId", warehouseId)
                .append("name", name)
                .append("description", description)
                .append("address1", address1)
                .append("address2", address2)
                .append("telephone1", telephone1)
                .append("telephone2", telephone2)
                .append("telephone3", telephone3)
                .append("geolocation", geolocation)
                .append("createDate", createDate)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .toString();
    }
}
