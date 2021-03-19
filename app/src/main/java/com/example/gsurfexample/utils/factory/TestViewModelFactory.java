package com.example.gsurfexample.utils.factory;


import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.ui.SurfSessionViewModel;

public class TestViewModelFactory  implements ViewModelProvider.Factory{
    static Application application;

    public TestViewModelFactory(Application myApplication) {
        application = myApplication;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SurfSessionViewModel(application);
    }
}