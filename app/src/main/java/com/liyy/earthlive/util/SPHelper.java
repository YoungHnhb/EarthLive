package com.liyy.earthlive.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.liyy.earthlive.MyApplication;

public class SPHelper {

    private Context mContext;
    private static final String SP_NAME = "EARTH_LIVE";

    public static SPHelper getInstance() {
        return Holder.singleton;
    }

    public void setStringSharePreference(String key, String value) {
        SharedPreferences preferences = getSysSP();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringSharePreference(String key, String defaultValue) {
        SharedPreferences preferences = getSysSP();
        return preferences.getString(key, defaultValue);
    }

    public void setLongSharePreference(String key, long value) {
        SharedPreferences preferences = getSysSP();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLongSharePreference(String key, long defaultValue) {
        SharedPreferences preferences = getSysSP();
        return preferences.getLong(key, defaultValue);
    }



    private SharedPreferences getSysSP() {
        return mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    private SPHelper() {
        mContext = MyApplication.getContext();
    }

    private static class Holder {
        private static SPHelper singleton = new SPHelper();
    }
}
