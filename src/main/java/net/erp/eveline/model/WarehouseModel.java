package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.OffsetDateTime;

public class WarehouseModel {
    private String id;
    private String name;
    private String description;
    private String address1;
    private String address2;
    private String telephone1;
    private String telephone2;
    private String geolocation;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public WarehouseModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public WarehouseModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public WarehouseModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public WarehouseModel setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public WarehouseModel setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public WarehouseModel setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
        return this;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public WarehouseModel setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
        return this;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public WarehouseModel setGeolocation(String geolocation) {
        this.geolocation = geolocation;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public WarehouseModel setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public WarehouseModel setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public WarehouseModel setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public WarehouseModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WarehouseModel that = (WarehouseModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(description, that.description)
                .append(address1, that.address1)
                .append(address2, that.address2)
                .append(telephone1, that.telephone1)
                .append(telephone2, that.telephone2)
                .append(geolocation, that.geolocation)
                .append(createDate, that.createDate)
                .append(lastModified, that.lastModified)
                .append(lastUser, that.lastUser)
                .append(enabled, that.enabled)
                .isEquals();
    }
}
