package com.jakester.twitterapp.util;

import com.jakester.twitterapp.interfaces.TwitterService;
import com.jakester.twitterapp.network.TwitterClient;

/**
 * Created by Jake on 11/15/2017.
 */

public class APIUtility {

    public static TwitterService getTwitterService() {
        return TwitterClient.getRetrofitClient().create(TwitterService.class);
    }
}
