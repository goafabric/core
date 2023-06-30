package org.goafabric.core.data.repository;

import org.goafabric.core.data.repository.entity.OrganizationEo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<OrganizationEo, String> {
    List<OrganizationEo> findByNameStartsWithIgnoreCase(String name);
}
