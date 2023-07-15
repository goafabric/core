package org.goafabric.core.ui.patient;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.goafabric.core.data.logic.PatientLogic;
import org.goafabric.core.ui.MainView;
import org.goafabric.core.ui.adapter.ChargeItemAdapter;
import org.goafabric.core.ui.adapter.DiagnosisAdapter;
import org.goafabric.core.ui.patient.tabs.MRCView;
import org.goafabric.core.ui.patient.tabs.PatientView;


@Route(value = "patient", layout = MainView.class)
@PageTitle("Patient")
public class PatientMainView extends VerticalLayout {

    public PatientMainView(
            PatientLogic patientLogic, DiagnosisAdapter diagnosisAdapter, ChargeItemAdapter chargeItemAdapter) {
        this.setSizeFull();

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("Patient", new PatientView(patientLogic::findByFamilyName));
        tabSheet.add("MRC", new MRCView(patientLogic,
                diagnosisAdapter::findByDisplay, chargeItemAdapter::findByDisplay));

        add(tabSheet);
    }
}