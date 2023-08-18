package org.goafabric.core.mrc.logic.mapper;

import org.goafabric.core.mrc.controller.vo.MedicalRecord;
import org.goafabric.core.mrc.repository.entity.MedicalRecordEo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicalRecordMapper {
    MedicalRecord map(MedicalRecordEo value);

    MedicalRecordEo map(MedicalRecord value);

    List<MedicalRecord> map(List<MedicalRecordEo> value);

    List<MedicalRecord> map(Iterable<MedicalRecordEo> value);
}