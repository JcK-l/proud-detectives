package de.uhh.detectives.backend.model.enumeration;

public enum Location implements HintItem {
    KUECHE("Kueche"),
    MUSIKZIMMER("Musikzimmer"),
    SCHLAFZIMMER("Schlafzimmer"),
    SPEISEZIMMER("Speisezimmer"),
    KELLER("Keller"),
    BILLARDZIMMER("Billardzimmer"),
    BIBLIOTHEK("Bibliothek"),
    GARTEN("Garten"),
    EINGANGSHALLE("Eingangshalle"),
    ARBEITSZIMMER("Arbeitszimmer");

    private final String label;

    Location(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return "Location";
    }
}
