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

    @Override
    public boolean equals(Object o){
        if(o instanceof HintModel){
            return (category.equals(((HintModel) o).getCategory()) && text.equals(((HintModel) o).getText()));
        }
        return false;
    }

    @Override
    public int hashCode(){
        return 2;
    }
}
