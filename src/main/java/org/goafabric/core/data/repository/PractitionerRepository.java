package org.goafabric.core.data.repository;

import org.goafabric.core.data.repository.entity.PractitionerEo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PractitionerRepository extends CrudRepository<PractitionerEo, String> {
    List<PractitionerEo> findByGivenNameStartsWithIgnoreCase(String givenName);

    List<PractitionerEo> findByFamilyNameStartsWithIgnoreCase(String familyName);
}

