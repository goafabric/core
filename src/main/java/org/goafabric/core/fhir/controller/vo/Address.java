
package org.goafabric.core.fhir.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String id;

    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String use;

    private List<String> line;
}
