package de.uhh.detectives.backend.model.enumeration;

public enum Culprit implements HintItem {
    DENNIS_GATOW("Dennis Gatow"),
    FELIX_BLOOM("Felix Bloom"),
    TOM_GRUEN("Tom Gruen"),
    KLARA_PORZ("Klara Porz"),
    GLORIA_ROTH("Gloria Roth"),
    DIANA_WEISS("Diana Weiss");

    private final String label;

    Culprit(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return "Person";
    }
}
