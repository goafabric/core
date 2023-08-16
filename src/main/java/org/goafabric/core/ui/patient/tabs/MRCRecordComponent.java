package org.goafabric.core.ui.patient.tabs;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.goafabric.core.data.repository.entity.PatientNamesOnly;
import org.goafabric.core.mrc.controller.vo.Encounter;
import org.goafabric.core.mrc.controller.vo.MedicalRecordType;
import org.goafabric.core.mrc.logic.EncounterLogic;
import org.goafabric.core.ui.adapter.ChargeItemAdapter;
import org.goafabric.core.ui.adapter.ConditionAdapter;
import org.goafabric.core.ui.adapter.vo.ChargeItem;
import org.goafabric.core.ui.adapter.vo.Condition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MRCRecordComponent {

    private final EncounterLogic encounterLogic;
    private final ConditionAdapter conditionAdapter;
    private final ChargeItemAdapter chargeItemAdapter;

    public MRCRecordComponent(EncounterLogic encounterLogic, ConditionAdapter conditionAdapter, ChargeItemAdapter chargeItemAdapter) {
        this.encounterLogic = encounterLogic;
        this.conditionAdapter = conditionAdapter;
        this.chargeItemAdapter = chargeItemAdapter;
    }


    public void processEncounters(VerticalLayout encounterLayout, List<PatientNamesOnly> patients, String display, MedicalRecordType recordType) {
        if (!patients.isEmpty()) {
            long start = System.currentTimeMillis();

            var patientId = patients.get(0).getId();
            var encounters = encounterLogic.findByPatientIdAndText(patientId, display);

            if (recordType != null) {
               encounters = encounters.stream().map(e ->
                       new Encounter(e.id(), e.patientId(), e.encounterDate(),
                        e.medicalRecords().stream().filter(record -> record.type().equals(recordType)).toList())).toList();
            }

            if (!encounters.isEmpty()) {
                encounters.forEach(encounter -> addMedicalRecords(encounterLayout, encounter));
            }

            Notification.show("Search took " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    private void addMedicalRecords(VerticalLayout encounterLayout, Encounter encounter) {
        encounter.medicalRecords().forEach(medicalRecord -> {
            if (encounterLayout.getChildren().count() < 100) {
                var typeCombo = new ComboBox<>("", MedicalRecordType.values());
                var textField = new TextField("", medicalRecord.display());
                textField.setWidth("500px");
                typeCombo.setValue(medicalRecord.type());
                encounterLayout.add(new HorizontalLayout(typeCombo, textField));
            }
        });
    }

    public void loadMedicalCatalog(String catalogType, ComboBox<String> filterCombo) {
        filterCombo.setItems((CallbackDataProvider.FetchCallback<String, String>) query -> {
            query.getLimit(); query.getOffset();
            var filter = query.getFilter().get();
            if (catalogType.equals("Diagnosis")) {
                return conditionAdapter.findByDisplay(filter).stream().map(Condition::display).limit(query.getLimit());
            }
            if (catalogType.equals("GOÄ")) {
                return chargeItemAdapter.findByDisplay(filter).stream().map(ChargeItem::display).limit(query.getLimit());
            }
            return new ArrayList<String>().stream();
        });
    }

}
