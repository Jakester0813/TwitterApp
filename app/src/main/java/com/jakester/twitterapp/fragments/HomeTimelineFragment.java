package com.jakester.twitterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.HomeTimelineActivity;
import com.jakester.twitterapp.activities.TweetActivity;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.interfaces.TweetTouchCallback;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.SimpleDividerItemDecoration;
import com.jakester.twitterapp.models.Tweet;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by Jake on 10/4/2017.
 */

public class HomeTimelineFragment extends BaseTimelineFragment implements TweetTouchCallback {

    //inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        mRecycler = (RecyclerView) v.findViewById(R.id.rv_tweets);
        mManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mManager);
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
        mRecycler.addOnScrollListener(scrollListener);
        mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
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
        swipeContainer.setColorSchemeResources(R.color.colorPrimary);

        mAdapter = new TweetAdapter(getContext(), this);
        mRecycler.setAdapter(mAdapter);



        return v;
    }



    public interface NewTweetCallback {
        void onTweetAction(Tweet tweet, boolean reply);
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






    @Override
    public void onClick(View view, Tweet tweet) {
        if(view.getId() == R.id.iv_favorite) {
            favoriteTweet(tweet);
        }
        else if (view.getId() == R.id.iv_retweet) {
            retweetTweet(tweet);
        }
        else if(view.getId() == R.id.iv_reply) {
            ((HomeTimelineActivity) getActivity()).showNewTweetDialog(true, tweet.getUser());
        }
        else{
            Intent tweetDetails = new Intent(getActivity(), TweetActivity.class);
            tweetDetails.putExtra("tweet", Parcels.wrap(tweet));
            startActivityForResult(tweetDetails, 1);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 5){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            addTweet(tweet);
        }
    }


}
