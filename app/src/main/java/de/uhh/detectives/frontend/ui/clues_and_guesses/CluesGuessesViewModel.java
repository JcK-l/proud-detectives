package de.uhh.detectives.frontend.ui.clues_and_guesses;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.lifecycle.ViewModel;

import java.util.List;

public class CluesGuessesViewModel extends ViewModel {

    public List<Cell> cells;
    public Drawable suspicion_left;
    public Drawable suspicion_middle;
    public Drawable suspicion_right;
    public int cardColor;
}