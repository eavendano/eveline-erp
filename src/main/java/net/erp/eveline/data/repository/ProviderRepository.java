package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProviderRepository extends JpaRepository<Provider, String> {

    boolean existsAllByProviderIdIn(final Set<String> providerIds);
}
