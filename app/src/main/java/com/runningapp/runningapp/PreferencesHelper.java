package com.runningapp.runningapp;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Named;

public class PreferencesHelper {

    private Context mContext;

    @Inject
    public PreferencesHelper(@Named("ApplicationContext") Context context) {
        mContext = context;
    }

    public String isWorking() {
        return "PreferencesHelper is working.";
    }
}
