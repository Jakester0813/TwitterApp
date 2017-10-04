package com.jakester.twitterapp.managers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jake on 10/3/2017.
 */

public class PrefsManager {
    private static PrefsManager mInstance = null;
    private static Context mContext;
    private SharedPreferences mPrefs;

    public static PrefsManager getInstance(Context pContext){
        if(mInstance == null){
            mInstance = new PrefsManager(pContext);
        }
        return mInstance;
    }

    public PrefsManager(Context pContext){
        mContext = pContext;
        mPrefs = mContext.getSharedPreferences("TwitterPrefs",Context.MODE_PRIVATE);
    }

    public void saveDraft(String pDraft){
        mPrefs.edit().putString("tweet_draft",pDraft).commit();
    }

    public String getDraft(){
       return mPrefs.getString("tweet_draft","");
    }
}
