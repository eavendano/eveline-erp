package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.ProductModel;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Quick note on this class, if the transaction is taking longer to resolve, that is because of the ManyToMany
// relationship between the Product and the Provider. A quick solution should be to remove the providerSet from the
// mapping process, create a toModel and/or toEntity methods that do not include the set mapping, adding indexes to
// speed up the transaction and re-evaluating how this relationship should be done in code.
// Also small details like including the set in the toString method might increment transaction throughput without the
// developer noticing.
public class ProductMapper {
    public static ProductModel toModel(final Product product) {
        return new ProductModel()
                .setId(product.getProductId())
                .setProviderSet(product.getProviderSet()
                        .stream()
                        .map(Provider::getProviderId)
                        .collect(Collectors.toSet()))
                .setTitle(product.getTitle())
                .setUpc(product.getUpc())
                .setDescription(product.getDescription())
                .setCreateDate(product.getCreateDate())
                .setLastModified(product.getLastModified())
                .setEnabled(product.getEnabled())
                .setLastUser(product.getLastUser());
    }

    public static Set<ProductModel> toModel(final Set<Product> products) {
        return products.stream()
                .map(ProductMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static ActiveProductModel toActiveModel(final Product product) {
        return new ActiveProductModel()
                .setId(product.getProductId())
                .setEnabled(product.getEnabled())
                .setLastUser(product.getLastUser());
    }

    public static Product toEntity(final ProductModel productModel, final Set<Provider> providers) {
        final Product entity = new Product()
                .setProductId(productModel.getId())

                .setProviderSet(providers)
                .setUpc(productModel.getUpc())
                .setTitle(productModel.getTitle())
                .setDescription(productModel.getDescription())
                .setCreateDate(productModel.getCreateDate())
                .setLastModified(productModel.getLastModified())
                .setEnabled(productModel.isEnabled())
                .setLastUser(productModel.getLastUser());

        if (Optional.ofNullable(productModel.isEnabled()).isPresent()) {
            entity.setEnabled(productModel.isEnabled());
        }
        return entity;
    }

    public static Product toEntity(final ProductModel productModel) {
        final Product entity = new Product()
                .setProductId(productModel.getId())
                .setUpc(productModel.getUpc())
                .setTitle(productModel.getTitle())
                .setDescription(productModel.getDescription())
                .setCreateDate(productModel.getCreateDate())
                .setLastModified(productModel.getLastModified())
                .setEnabled(productModel.isEnabled())
                .setLastUser(productModel.getLastUser());

        if (Optional.ofNullable(productModel.isEnabled()).isPresent()) {
            entity.setEnabled(productModel.isEnabled());
        }
        return entity;
    }

    public static Set<Product> toEntity(final Set<ProductModel> productModelSet) {
        return productModelSet
                .stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static Product toEntity(final Product product, final ActiveProductModel activeProductModel) {
        return product
                .setLastUser(activeProductModel.getLastUser())
                .setEnabled(activeProductModel.isEnabled());
    }

}
