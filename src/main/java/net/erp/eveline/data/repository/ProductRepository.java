package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByUpc(final String upc);

    Set<Product> findByProviderSetProviderId(final String providerId);
}
