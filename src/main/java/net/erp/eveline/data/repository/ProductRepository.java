package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository  extends JpaRepository<Product, String> {
    Optional<Product> findByUpc(final String upc);
    Set<Product> findByProviderSetProviderId(final String providerId);

//    @Query(value = "SELECT * FROM product p " +
//            "JOIN product_provider_assignation ppa USING(product_id) " +
//            "WHERE ppa.provider_id = ?1", nativeQuery = true)
//    Set<Product> findProductsByProviderId(final String providerId);
}
