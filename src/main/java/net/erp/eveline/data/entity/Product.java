package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator = "productIdGenerator")
    @GenericGenerator(name = "productIdGenerator", strategy = "net.erp.eveline.data.generators.CustomGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "prefix", value = "s"), @org.hibernate.annotations.Parameter(name = "sequence", value = "product_id_seq")})
    private String productId;

    @ManyToOne
    @JoinColumn(name="brand_id", nullable=false)
    private Brand brand;

    @OneToMany(cascade = MERGE, fetch = LAZY)
    @JoinTable(
            name = "product_provider_assignation",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_id"))
    private Set<Provider> providerSet;

    @Column(name = "upc")
    private String upc;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "sanitary_registry_number")
    private String sanitaryRegistryNumber;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "last_modified")
    private OffsetDateTime lastModified;

    @Column(name = "last_user")
    private String lastUser;

    @Column(name = "enabled")
    private Boolean enabled = true;

    public String getProductId() {
        return productId;
    }

    public Product setProductId(final String productId) {
        this.productId = productId;
        return this;
    }

    public Brand getBrand() {
        return brand;
    }

    public Product setBrand(Brand brand) {
        this.brand = brand;
        return this;
    }

    public Set<Provider> getProviderSet() {
        return providerSet;
    }

    public Product setProviderSet(final Set<Provider> providerSet) {
        this.providerSet = providerSet;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Product setTitle(final String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getSanitaryRegistryNumber() {
        return sanitaryRegistryNumber;
    }

    public Product setSanitaryRegistryNumber(final String sanitaryRegistryNumber) {
        this.sanitaryRegistryNumber = sanitaryRegistryNumber;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public Product setCreateDate(final OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public Product setLastModified(final OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Product setLastUser(final String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Product setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getUpc() {
        return upc;
    }

    public Product setUpc(final String upc) {
        this.upc = upc;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return new EqualsBuilder()
                .append(productId, product.productId)
                .append(brand, product.brand)
                .append(providerSet, product.providerSet)
                .append(upc, product.upc)
                .append(title, product.title)
                .append(description, product.description)
                .append(sanitaryRegistryNumber, product.sanitaryRegistryNumber)
                .append(createDate, product.createDate)
                .append(lastModified, product.lastModified)
                .append(lastUser, product.lastUser)
                .append(enabled, product.enabled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(productId)
                .append(brand)
                .append(providerSet)
                .append(upc)
                .append(title)
                .append(description)
                .append(sanitaryRegistryNumber)
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
                .append("productId", productId)
                .append("brand", brand)
                .append("upc", upc)
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
