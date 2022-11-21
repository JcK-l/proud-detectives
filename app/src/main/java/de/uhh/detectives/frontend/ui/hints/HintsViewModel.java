package de.uhh.detectives.frontend.ui.hints;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HintsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HintsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TBD");
        // TODO add view model
    }

    public LiveData<String> getText() {
        return mText;
    }
}