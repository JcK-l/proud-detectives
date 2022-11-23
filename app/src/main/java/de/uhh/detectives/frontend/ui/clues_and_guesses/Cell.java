package de.uhh.detectives.frontend.ui.clues_and_guesses;

public class Cell {
    private CellState state;
    private final int image;
    private final String category;

    public Cell(CellState state, final int image, final String category){
        this.state = state;
        this.image = image;
        this.category = category;
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
