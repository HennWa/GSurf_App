package com.example.gsurfexample.utils.factory;


import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.gsurfexample.ui.TimeSampleViewModel;

public class TimeSampleViewModelFactory implements ViewModelProvider.Factory{
    static Application application;

    public TimeSampleViewModelFactory(Application myApplication) {
        application = myApplication;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TimeSampleViewModel(application);
    }
}