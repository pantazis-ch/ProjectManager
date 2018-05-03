package com.android.example.projectmanager.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.example.projectmanager.R;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static String getQuote(Context context) {

        String key = context.getString(R.string.pref_get_quote);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String quote = prefs.getString(key, context.getString(R.string.label_na));

        return quote;
    }

    public static void setQuote(Context context, String quote ) {
        String key = context.getString(R.string.pref_get_quote);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, quote);
        editor.apply();
    }

    public static String getImageLink(Context context) {

        String key = context.getString(R.string.pref_get_image_link);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String quote = prefs.getString(key, context.getString(R.string.label_na));

        return quote;
    }

    public static void setImageLink(Context context, String imageLink ) {
        String key = context.getString(R.string.pref_get_image_link);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, imageLink);
        editor.apply();
    }

    public static String getAuthor(Context context) {

        String key = context.getString(R.string.pref_get_author);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String quote = prefs.getString(key, context.getString(R.string.label_na));

        return quote;
    }

    public static void setAuthor(Context context, String author ) {
        String key = context.getString(R.string.pref_get_author);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, author);
        editor.apply();
    }

    public static String getDate(Context context) {

        String key = context.getString(R.string.pref_get_date);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String quote = prefs.getString(key, context.getString(R.string.label_na));

        return quote;
    }

    public static void setDate(Context context, String date ) {
        String key = context.getString(R.string.pref_get_date);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, date);
        editor.apply();
    }

    public static void setCurrentProjectPosition(Context context, int projectPosition ) {
        String key = context.getString(R.string.pref_current_project_position_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, projectPosition);
        editor.apply();
    }

    public static int getCurrentProjectPosition(Context context) {
        String key = context.getString(R.string.pref_current_project_position_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(key, 0);
    }


    public static void setProjectCount(Context context, int projectCount ) {
        String key = context.getString(R.string.pref_project_count_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, projectCount);
        editor.apply();
    }

    public static int getProjectCount(Context context) {
        String key = context.getString(R.string.pref_project_count_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(key, 0);
    }

}
