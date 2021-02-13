package net.erp.eveline.data.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator = "productIdGenerator")
    @GenericGenerator(name = "productIdGenerator", parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "s"),
            strategy = "net.erp.eveline.data.generators.ProductIdGenerator")
    private String productId;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(name="upc")
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
    private Boolean enabled;

    public String getProductId() {
        return productId;
    }

    public Product setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public Provider getProvider() {
        return provider;
    }

    public Product setProvider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Product setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSanitaryRegistryNumber() {
        return sanitaryRegistryNumber;
    }

    public Product setSanitaryRegistryNumber(String sanitaryRegistryNumber) {
        this.sanitaryRegistryNumber = sanitaryRegistryNumber;
        return this;
    }

    public OffsetDateTime getCreateDate() {
        return createDate;
    }

    public Product setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
        return this;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public Product setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastUser() {
        return lastUser;
    }

    public Product setLastUser(String lastUser) {
        this.lastUser = lastUser;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Product setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getUpc() {
        return upc;
    }

    public Product setUpc(String upc) {
        this.upc = upc;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return new EqualsBuilder()
                .append(productId, product.productId)
                .append(provider, product.provider)
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
                .append(provider)
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
}
