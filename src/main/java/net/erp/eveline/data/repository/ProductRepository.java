package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProductRepository  extends JpaRepository<Product, String> {
    Set<Product> findAllByProviderProviderId(final String providerId);
    Product findByUpc(final String upc);
}
