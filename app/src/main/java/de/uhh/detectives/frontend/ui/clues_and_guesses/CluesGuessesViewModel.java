package de.uhh.detectives.frontend.ui.clues_and_guesses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CluesGuessesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CluesGuessesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TBD");
        // TODO add view model
    }

    public LiveData<String> getText() {
        return mText;
    }
}