package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "provider")
public class Provider {

    @Id
    @GeneratedValue(generator = "providerIdGenerator")
    @GenericGenerator(name = "providerIdGenerator", strategy = "net.erp.eveline.data.generators.CustomGenerator",
            parameters = {@Parameter(name = "prefix", value = "p"), @Parameter(name = "sequence", value = "provider_id_seq")})
    @Column(name = "provider_id")
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
    private Boolean enabled = false;

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

    public Provider setProviderId(final String providerId) {
        this.providerId = providerId;
        return this;
    }

    public Provider setName(final String name) {
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

    public Provider setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Provider provider = (Provider) o;

        return new EqualsBuilder()
                .append(providerId, provider.providerId)
                .append(name, provider.name)
                .append(description, provider.description)
                .append(email, provider.email)
                .append(telephone1, provider.telephone1)
                .append(telephone2, provider.telephone2)
                .append(telephone3, provider.telephone3)
                .append(createDate, provider.createDate)
                .append(lastModified, provider.lastModified)
                .append(lastUser, provider.lastUser)
                .append(enabled, provider.enabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(providerId)
                .append(name)
                .append(description)
                .append(email)
                .append(telephone1)
                .append(telephone2)
                .append(telephone3)
                .append(createDate)
                .append(lastModified)
                .append(lastUser)
                .append(enabled)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
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
