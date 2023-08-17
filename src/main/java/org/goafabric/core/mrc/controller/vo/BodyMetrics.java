package org.goafabric.core.mrc.controller.vo;

public record BodyMetrics(
        String id,
        String bodyHeight,
        String bellyCircumference,
        String headCircumference,
        String bodyFat
) {
    public String toDisplay() {
        return "Body Height: " + bodyHeight +
                " Belly Circumference: " + bellyCircumference +
                " Head Circumference: " + headCircumference +
                " Body Fat: " + bodyFat;
    }
}
