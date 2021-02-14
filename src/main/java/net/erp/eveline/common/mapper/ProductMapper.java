package net.erp.eveline.common.mapper;

import net.erp.eveline.data.entity.Product;
import net.erp.eveline.data.entity.Provider;
import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ProductModel;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapper {
    public static ProductModel toModel(final Product product){
        return new ProductModel()
                .setId(product.getProductId())
                .setProviderId(product.getProvider().getProviderId())
                .setTitle(product.getTitle())
                .setUpc(product.getUpc())
                .setDescription(product.getDescription())
                .setCreateDate(product.getCreateDate())
                .setLastModified(product.getLastModified())
                .setEnabled(product.getEnabled())
                .setLastUser(product.getLastUser());
    }

    public static Set<ProductModel> toModel(final Set<Product> products){
        return products.stream()
                .map(ProductMapper::toModel)
                .collect(Collectors.toSet());
    }

    public static Product toEntity(final ProductModel productModel){
        final Product entity = new Product()
                .setProductId(productModel.getId())
                .setProvider(new Provider().setProviderId(productModel.getProviderId()))
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

    public static Set<Product> toEntity(final Set<ProductModel> productModelSet){
        return productModelSet
                .stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static Product toEntity(final Product product, final ActivateProductModel activateProductModel){
        return product
                .setLastUser(activateProductModel.getLastUser())
                .setEnabled(activateProductModel.isEnabled());
    }

}
