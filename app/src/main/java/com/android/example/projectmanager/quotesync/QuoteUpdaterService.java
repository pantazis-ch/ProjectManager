package com.android.example.projectmanager.quotesync;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.android.example.projectmanager.utilities.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class QuoteUpdaterService extends IntentService {

    private static final String TAG = "QuoteUpdaterService";

    public static final String BROADCAST_DATA_UPDATED = "com.android.example.projectmanager.intent.action.DATA_UPDATED";
    public static final String BROADCAST_NO_CONNECTIVITY = "com.android.example.projectmanager.intent.action.NO_CONNECTIVITY";
    public static final String BROADCAST_DATA_UPDATE_FAILED = "com.android.example.projectmanager.intent.action.DATA_UPDATE_FAILED";

    public QuoteUpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_NO_CONNECTIVITY));
            return;
        }

        URL url = NetworkUtility.buildUrl();
        String httpResponse = null;

        try {
            httpResponse = NetworkUtility.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            Intent syncErrorIntent = new Intent(BROADCAST_DATA_UPDATE_FAILED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(syncErrorIntent);
            return;
        }

        JSONObject response = null;
        JSONObject contents = null;
        JSONArray quotes = null;
        try {
            response = new JSONObject(httpResponse);
            contents = response.getJSONObject("contents");
            quotes = contents.getJSONArray("quotes");
        } catch (JSONException e) {
            Intent syncErrorIntent = new Intent(BROADCAST_DATA_UPDATE_FAILED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(syncErrorIntent);
            return;
        }

        for (int i=0; i<quotes.length(); i++) {

            JSONObject quoteJSONObject = null;
            try {
                quoteJSONObject = quotes.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String quote = null;
            String author = null;
            String date = null;
            String background = null;
            try {
                quote = quoteJSONObject.getString("quote");
                author = quoteJSONObject.getString("author");
                date = quoteJSONObject.getString("date");
                background = quoteJSONObject.getString("background");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(quote != null && !quote.isEmpty()) {
                PrefUtils.setQuote(this, quote);
                if (author != null && !author.isEmpty()) {
                    PrefUtils.setAuthor(this, author);
                }
                if (date != null && !date.isEmpty()) {
                    PrefUtils.setDate(this, date);
                }
                if (background != null && !background.isEmpty()) {
                    PrefUtils.setImageLink(this, background);
                }
            } else {
                Intent syncErrorIntent = new Intent(BROADCAST_DATA_UPDATE_FAILED);
                LocalBroadcastManager.getInstance(this).sendBroadcast(syncErrorIntent);
                return;
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_DATA_UPDATED));

        }
    }
}
