package com.wudaokou.easylearn.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    private MutableLiveData<String> pText;
    private MutableLiveData<Boolean> succ;

    public UserViewModel() {
        mText = new MutableLiveData<>();
        pText = new MutableLiveData<>();
        succ = new MutableLiveData<>();
        mText.setValue("This is user id");
        pText.setValue("this is user password");
        succ.setValue(false);
    }

    public void setmText(String mt) {
        this.mText.setValue(mt);
    }
    public void setpText(String pt) {
        this.pText.setValue(pt);
    }
    public void setsucc(Boolean s) {
        succ.setValue(s);
    }

    public LiveData<String> getmText() {
        return mText;
    }
    public LiveData<String> getpText() {
        return pText;
    }
    public LiveData<Boolean> getsucc() {
        return succ;
    }
}