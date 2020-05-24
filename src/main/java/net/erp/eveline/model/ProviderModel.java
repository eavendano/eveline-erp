package net.erp.eveline.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.OffsetDateTime;

public class ProviderModel {

    private String id;
    private String description;
    private String email;
    private String telephone1;
    private String telephone2;
    private String telephone3;
    private OffsetDateTime createDate;
    private OffsetDateTime lastModified;
    private String lastUser;

    public String getId() {
        return id;
    }

    public ProviderModel setId(final String id) {
        this.id = id;
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
                .toString();
    }
}
