package net.erp.eveline.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class ProviderModel {

    private String id;
    private String name;
    private String description;
    private String email;
    private String telephone1;
    private String telephone2;
    private String telephone3;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public ProviderModel setId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProviderModel setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProviderModel setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ProviderModel setEmail(final String email) {
        this.email = email;
        return this;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public ProviderModel setTelephone1(final String telephone1) {
        this.telephone1 = telephone1;
        return this;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public ProviderModel setTelephone2(final String telephone2) {
        this.telephone2 = telephone2;
        return this;
    }

    public String getTelephone3() {
        return telephone3;
    }

    public ProviderModel setTelephone3(final String telephone3) {
        this.telephone3 = telephone3;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public ProviderModel setCreateDate(final OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public ProviderModel setLastModified(final OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public ProviderModel setLastUser(final String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public ProviderModel setEnabled(final Boolean enabled) {
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

        ProviderModel that = (ProviderModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(description, that.description)
                .append(email, that.email)
                .append(telephone1, that.telephone1)
                .append(telephone2, that.telephone2)
                .append(telephone3, that.telephone3)
                .append(createDate, that.createDate)
                .append(lastModified, that.lastModified)
                .append(lastUser, that.lastUser)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("id", id)
                .append("description", description)
                .append("email", email)
                .append("telephone1", telephone1)
                .append("telephone2", telephone2)
                .append("telephone3", telephone3)
                .append("createDate", createDate)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .append("enabled", enabled)
                .toString();
    }
}
