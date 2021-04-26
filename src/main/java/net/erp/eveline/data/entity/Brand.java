package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.time.OffsetDateTime;

@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @GeneratedValue(generator = "brandIdGenerator")
    @GenericGenerator(name = "brandIdGenerator", strategy = "net.erp.eveline.data.generators.CustomGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "prefix", value = "b"), @org.hibernate.annotations.Parameter(name = "sequence", value = "brand_id_seq")})

    @Column(name = "brand_id")
    private String brandId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "last_modified")
    private OffsetDateTime lastModified;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "last_user")
    private String lastUser;

    public String getBrandId() {
        return brandId;
    }

    public Brand setBrandId(String brandId) {
        this.brandId = brandId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Brand setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Brand setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Brand setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public Brand setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public Brand setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Brand setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Brand brand = (Brand) o;

        return new EqualsBuilder()
                .append(brandId, brand.brandId)
                .append(name, brand.name)
                .append(description, brand.description)
                .append(createDate, brand.createDate)
                .append(enabled, brand.enabled)
                .append(lastModified, brand.lastModified)
                .append(lastUser, brand.lastUser)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("__class__", this.getClass().getSimpleName())
                .append("brandId", brandId)
                .append("name", name)
                .append("description", description)
                .append("createDate", createDate)
                .append("enabled", enabled)
                .append("lastModified", lastModified)
                .append("lastUser", lastUser)
                .toString();
    }
}
