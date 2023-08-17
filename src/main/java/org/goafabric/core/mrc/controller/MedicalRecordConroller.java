package org.goafabric.core.mrc.controller;

import jakarta.validation.Valid;
import org.goafabric.core.mrc.controller.vo.MedicalRecord;
import org.goafabric.core.mrc.logic.MedicalRecordLogic;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/medicalrecords", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class MedicalRecordConroller {
    private final MedicalRecordLogic logic;

    public MedicalRecordConroller(MedicalRecordLogic logic) {
        this.logic = logic;
    }

    @GetMapping("getById/{id}")
    public MedicalRecord getById(@PathVariable("id") String id) {
        return logic.getById(id);
    }


    @PostMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody @Valid MedicalRecord medicalRecord) {
        logic.save(medicalRecord);
    }

}
