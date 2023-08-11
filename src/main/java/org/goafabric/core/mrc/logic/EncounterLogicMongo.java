package org.goafabric.core.mrc.logic;

import org.goafabric.core.mrc.controller.vo.Encounter;
import org.goafabric.core.mrc.repository.EncounterRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("mongodb")
public class EncounterLogicMongo implements EncounterLogic {
    private final EncounterMapper encounterMapper;

    private final EncounterRepository encounterRepository;

    public EncounterLogicMongo(EncounterMapper encounterMapper, EncounterRepository encounterRepository) {
        this.encounterMapper = encounterMapper;
        this.encounterRepository = encounterRepository;
    }

    public void save(Encounter encounter) {
        encounterRepository.save(encounterMapper.map(encounter));
    }

    public List<Encounter> findByPatientIdAndText(String patientId, String text) {
        return encounterMapper.map(encounterRepository.findAllByPatientId(patientId, (new TextCriteria().matchingAny(text))));
    }

}
