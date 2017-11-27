package com.jakester.twitterapp.interfaces;

import android.view.View;

import com.jakester.twitterapp.models.Tweet;

/**
 * Created by Jake on 10/5/2017.
 */

public interface TweetTouchCallback {
    void onClick(View view, Tweet tweet);
}
