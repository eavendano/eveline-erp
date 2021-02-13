package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class ProductModel {
    private String id;
    private String upc;
    private ProviderModel providerModel;
    private String title;
    private String description;
    private String sanitaryRegistryNumber;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public ProductModel setId(String id) {
        this.id = id;
        return this;
    }

    public ProviderModel getProviderModel() {
        return providerModel;
    }

    public ProductModel setProviderModel(ProviderModel providerModel) {
        this.providerModel = providerModel;
        return this;
    }


    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getTitle() {
        return title;
    }

    public ProductModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProductModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSanitaryRegistryNumber() {
        return sanitaryRegistryNumber;
    }

    public ProductModel setSanitaryRegistryNumber(String sanitaryRegistryNumber) {
        this.sanitaryRegistryNumber = sanitaryRegistryNumber;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public ProductModel setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public ProductModel setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public ProductModel setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public ProductModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProductModel that = (ProductModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(upc, that.upc)
                .append(providerModel, that.providerModel)
                .append(title, that.title)
                .append(description, that.description)
                .append(sanitaryRegistryNumber, that.sanitaryRegistryNumber)
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
                .append("upc", upc)
                .append("providerModel", providerModel)
                .append("title", title)
                .append("description", description)
                .append("sanitaryRegistryNumber", sanitaryRegistryNumber)
                .append("createDate", createDate)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .append("enabled", enabled)
                .toString();
    }
}