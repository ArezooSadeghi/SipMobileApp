package com.example.sipmobileapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SipMobileAppPreferences {

    private static final String USER_LOGIN_KEY = "userLoginKey";
    private static final String CENTER_NAME = "centerName";
    private static final String USERNAME = "userName";

    public static String getUserLoginKey(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(USER_LOGIN_KEY, null);
    }

    public static void setUserLoginKey(Context context, String userLoginKey) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(USER_LOGIN_KEY, userLoginKey).commit();
    }

    public static String getCenterName(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(CENTER_NAME, null);
    }

    public static void setCenterName(Context context, String centerName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(CENTER_NAME, centerName).commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(USERNAME, null);
    }

    public static void setUsername(Context context, String userName) {
        SharedPreferences preferences = getSharedPreferences(context);
        preferences.edit().putString(USERNAME, userName).commit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getPackageName(),
                context.MODE_PRIVATE);
    }
}
