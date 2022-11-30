package de.uhh.detectives.backend.model.enumeration;

public enum Weapon implements HintItem {
    PISTOLE("Pistole"),
    DOLCH("Dolch"),
    SEIL("Seil"),
    KERZENLEUCHTER("Kerzenleuchter"),
    ROHRZANGE("Rohrzange"),
    HEIZUNGSROHR("Heizungsrohr"),
    MESSER("Messer"),
    GIFT("Gift"),
    HANTEL("Hantel");

    private final String label;

    Weapon(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return "Weapon";
    }
}
