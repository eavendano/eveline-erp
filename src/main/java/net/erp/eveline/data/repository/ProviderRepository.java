package net.erp.eveline.data.repository;

import net.erp.eveline.data.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProviderRepository extends JpaRepository<Provider, String> {

    @Query(value = "SELECT nextval('provider_id_seq')", nativeQuery = true)
    Long getProvideIdNextVal();
}
