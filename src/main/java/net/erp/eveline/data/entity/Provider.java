package net.erp.eveline.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "provider")
public class Provider {

    @Id
    private String providerId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone1")
    private String telephone1;

    @Column(name = "telephone2")
    private String telephone2;

    @Column(name = "telephone3")
    private String telephone3;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "last_modified")
    private OffsetDateTime lastModified;

    @Column(name = "last_user")
    private String lastUser;

    @Column(name = "enabled")
    private Boolean enabled;

    public String getProviderId() {
        return providerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public String getTelephone3() {
        return telephone3;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public Provider setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public Provider setName(String name) {
        this.name = name;
        return this;
    }

    public Provider setDescription(final String description) {
        this.description = description;
        return this;
    }

    public Provider setEmail(final String email) {
        this.email = email;
        return this;
    }

    public Provider setTelephone1(final String telephone1) {
        this.telephone1 = telephone1;
        return this;
    }

    public Provider setTelephone2(final String telephone2) {
        this.telephone2 = telephone2;
        return this;
    }

    public Provider setTelephone3(final String telephone3) {
        this.telephone3 = telephone3;
        return this;
    }

    public Provider setLastUser(final String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Provider setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this, org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("providerId", providerId)
                .append("name", name)
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
