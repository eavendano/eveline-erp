package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class BrandModel {
    private String id;
    private String name;
    private String description;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public BrandModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BrandModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BrandModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public BrandModel setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public BrandModel setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public BrandModel setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public BrandModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BrandModel that = (BrandModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(description, that.description)
                .append(createDate, that.createDate)
                .append(lastModified, that.lastModified)
                .append(lastUser, that.lastUser)
                .append(enabled, that.enabled)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("id", id)
                .append("name", name)
                .append("description", description)
                .append("createDate", createDate)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .append("enabled", enabled)
                .toString();
    }
}
