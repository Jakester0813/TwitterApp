package com.jakester.twitterapp.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jakester.twitterapp.util.TwitterContstants;

import java.io.IOException;

/**
 * Created by Jake on 9/24/2017.
 */

public class InternetManager {

    private static InternetManager mInstance = null;
    private static Context mContext;
    private boolean mFromWeb;

    public static InternetManager getInstance(Context pContext){
        if(mInstance == null){
            mInstance = new InternetManager(pContext);
        }
        return mInstance;
    }

    public InternetManager(Context pContext){
        mContext = pContext;
        mFromWeb = false;
    }


    public boolean isInternetAvailable(){
        return isNetworkAvailable();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec(TwitterContstants.NETWORK_EXEC);
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }



    public AlertDialog noInternetDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        builder1.setTitle(TwitterContstants.NO_INTERNET_TITLE);
        builder1.setMessage(TwitterContstants.NO_INTERNET_TEXT);
        builder1.setCancelable(true);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder1.create();
    }

    public void switchFromWeb(boolean value){
        mFromWeb = value;
    }

    public boolean getFromWeb(){
        return mFromWeb;
    }
}
