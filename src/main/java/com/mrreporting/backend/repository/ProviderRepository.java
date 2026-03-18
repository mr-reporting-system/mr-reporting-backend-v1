package com.mrreporting.backend.repository;

import com.mrreporting.backend.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    // We can add a custom finder to filter by type (Chemist vs Stockist)
    List<Provider> findByType(String type);

    // Or find all providers in a specific area
    List<Provider> findByAreaId(Long areaId);
}