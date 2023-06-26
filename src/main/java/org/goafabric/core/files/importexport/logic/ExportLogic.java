package org.goafabric.core.files.importexport.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goafabric.core.data.logic.OrganizationLogic;
import org.goafabric.core.data.logic.PatientLogic;
import org.goafabric.core.data.logic.PractitionerLogic;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class ExportLogic {
    private final PatientLogic patientLogic;
    private final PractitionerLogic practitionerLogic;
    private final OrganizationLogic organizationLogic;

    public ExportLogic(PatientLogic patientLogic, PractitionerLogic practitionerLogic, OrganizationLogic organizationLogic) {
        this.patientLogic = patientLogic;
        this.practitionerLogic = practitionerLogic;
        this.organizationLogic = organizationLogic;
    }

    public void run(String path) {
        if (!Files.exists(Paths.get(path))) {
            throw new IllegalStateException("Path not available");
        }

        try {
            exportPatient(path);
            exportPractitioners(path);
            exportOrganizations(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportPatient(String path) throws IOException {
        var patients = patientLogic.findAll();
        Files.writeString(Paths.get(path + "/patient.json"),
                new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(patients));
    }

    private void exportPractitioners(String path) throws IOException {
        new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new JavaTimeModule());
        var practitioners = practitionerLogic.findAll();
        Files.writeString(Paths.get(path + "/practitioner.json"),
                new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(practitioners));
    }

    private void exportOrganizations(String path) throws IOException {
        var organizations = organizationLogic.findAll();
        Files.writeString(Paths.get(path + "/organization.json"),
                new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(organizations));
    }

}
