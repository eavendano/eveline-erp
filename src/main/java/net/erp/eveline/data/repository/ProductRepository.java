package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository  extends JpaRepository<Product, String> {
}
