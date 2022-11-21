package de.uhh.detectives.frontend.ui.hints;

public class HintModel {

    private final String category;
    private final String text;
    private final int image;

    public HintModel(final String category, final String text, final int image) {
        this.category = category;
        this.text = text;
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }

    public int getImage() {
        return image;
    }
}
