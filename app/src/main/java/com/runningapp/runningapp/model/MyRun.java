package com.runningapp.runningapp.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class MyRun {

    private Drawable mStaticMap;
    private String mDistance;
    private String mDuration;
    private String mMileSplit;
    private String mDate;

    public Drawable getStaticMap() {
        return mStaticMap;
    }

    public void setStaticMap(Drawable staticMap) {
        mStaticMap = staticMap;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public String getMileSplit() {
        return mMileSplit;
    }

    public void setMileSplit(String mileSplit) {
        mMileSplit = mileSplit;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }
}
