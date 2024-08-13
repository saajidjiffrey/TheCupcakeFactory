package com.example.thecupcakefactory.ui.cupcakes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CupcakeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CupcakeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}