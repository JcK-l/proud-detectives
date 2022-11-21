package de.uhh.detectives.frontend.ui.comms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CommsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CommsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TBD");
        // TODO add view model
    }

    public LiveData<String> getText() {
        return mText;
    }
}