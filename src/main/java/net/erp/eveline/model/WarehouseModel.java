package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class WarehouseModel {
    private String id;
    private String description;
    private String location;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;

    public String getId() {
        return id;
    }

    public WarehouseModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public WarehouseModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public WarehouseModel setLocation(String location) {
        this.location = location;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WarehouseModel that = (WarehouseModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(description, that.description)
                .append(location, that.location)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("id", id)
                .append("description", description)
                .append("location", location)
                .toString();
    }
}
