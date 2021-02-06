package net.erp.eveline.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class ProductModel {
    private String id;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public ProductModel setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("id", id)
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
