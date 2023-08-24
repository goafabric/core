package org.goafabric.core.organization.controller.vo;

import java.util.List;

public record User(
        String id,
        String version,
        String patientId,
        String name,
        List<Role> roles
) {}
