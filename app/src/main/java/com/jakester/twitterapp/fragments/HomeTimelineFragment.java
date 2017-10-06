package com.jakester.twitterapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.HomeTimelineActivity;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.listener.TweetTouchCallback;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jake on 10/4/2017.
 */

public class HomeTimelineFragment extends Fragment  implements NewTweetDialogFragment.FilterDialogListener, TweetTouchCallback {

    ArrayList<Tweet> tweets;
    TweetAdapter mAdapter;
    RecyclerView mTweetRecycler;
    LinearLayoutManager mManager;
    private SwipeRefreshLayout swipeContainer;
    private EndlessScrollListener scrollListener;
    private TwitterClient client;

    //inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        mTweetRecycler = (RecyclerView) v.findViewById(R.id.rv_tweets);
        mManager = new LinearLayoutManager(getContext());
        mTweetRecycler.setLayoutManager(mManager);
        scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(getContext()).isInternetAvailable()) {
                    populateTimeline(page+1,false);
                }
                else{
                    ((HomeTimelineActivity)getActivity()).showNoInternetDialog();
                }
            }
        };
        mTweetRecycler.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.srl_swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(0, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        mAdapter = new TweetAdapter(getContext(), this);
        mTweetRecycler.setAdapter(mAdapter);



        return v;
    }

    public void populateTimeline(int page, final boolean refresh){
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addItems(Tweet.fromJson(response),refresh);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onFinishFilterDialog(final Tweet tweet) {
        client.postTweet(tweet.getTweet(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                addTweet(tweet);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                addTweet(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Override
    public void onResume() {
        super.onResume();
        client = TwitterApplication.getRestClient();
        if(client.isAuthenticated() && InternetManager.getInstance(getActivity()).isInternetAvailable()) {
            populateTimeline(0, false);
        }
        if(!InternetManager.getInstance(getActivity()).isInternetAvailable()){
            ArrayList<Tweet> tweets = (ArrayList<Tweet>) SQLite.select()
                    .from(Tweet.class).queryList();
            mAdapter.addTweets(tweets);
            swipeContainer.setRefreshing(false);
        }
    }



    public void addItems(ArrayList<Tweet> tweets, boolean refresh){
        mAdapter.addTweets(tweets);
        swipeContainer.setRefreshing(false);
    }

    public void addTweet(Tweet tweet) {
        mAdapter.addTweet(tweet);
        mTweetRecycler.scrollToPosition(0);
    }


    @Override
    public void onClick(View view, Tweet tweet) {
        if(view.getId() == R.id.iv_favorite) {
            favoriteTweet(tweet);
        }
        else if (view.getId() == R.id.iv_retweet) {
            retweetTweet(tweet);
        }
        else if(view.getId() == R.id.iv_reply) {
            //replyTweet(tweet.getFavorited(), tweet.getTweetId());
        }
    }

    private void favoriteTweet(final Tweet tweet){
        client.favoriteTweet(tweet.getFavorited(), tweet.getTweetId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                tweet.setFavorited(!tweet.getFavorited());
                tweet.setFavoritedCount(tweet.getFavorited());
                tweet.save();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                //addTweet(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }

    private void retweetTweet(final Tweet tweet){
        client.favoriteTweet(tweet.getRetweeted(), tweet.getTweetId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                tweet.setRetweeted(!tweet.getRetweeted());
                tweet.setRetweetCount(tweet.getRetweeted());
                tweet.save();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                //addTweet(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }
}
