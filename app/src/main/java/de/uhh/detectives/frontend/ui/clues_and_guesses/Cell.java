package de.uhh.detectives.frontend.ui.clues_and_guesses;

public class Cell {
    private CellState state;
    private final int image;
    private final String category;
    private final String description;

    public Cell(CellState state, final int image, final String category, final String description){
        this.state = state;
        this.image = image;
        this.category = category;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public int getImage() {
        return image;
    }
}
