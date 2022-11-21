package de.uhh.detectives.frontend.ui.maps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MapsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("TBD");
        // TODO add view model
    }

    public LiveData<String> getText() {
        return mText;
    }
}