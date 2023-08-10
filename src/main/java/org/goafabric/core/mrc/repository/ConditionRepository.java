package org.goafabric.core.mrc.repository;


import org.goafabric.core.mrc.repository.entity.ConditionEo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConditionRepository extends CrudRepository<ConditionEo, String> {
    //@Query(nativeQuery = true, value = "select * from condition WHERE encounter_id = :encounterId and to_tsvector('english', display) @@ to_tsquery('english', :display)")
    List<ConditionEo> findByEncounterIdAndDisplayContainsIgnoreCase(String encounterId, String display);

    @Query("select c from ConditionEo c")
    List<ConditionEo> findAllByEncounterId(String encounterId, TextCriteria criteria);

}

