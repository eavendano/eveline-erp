package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface BrandRepository extends JpaRepository<Brand, String> {
    boolean existsAllByBrandIdIn(final Set<String> brandsId);
}
