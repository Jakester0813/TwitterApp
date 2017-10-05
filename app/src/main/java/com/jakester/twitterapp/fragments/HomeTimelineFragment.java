package com.jakester.twitterapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.HomeTimelineActivity;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.network.TwitterClient;

import java.util.ArrayList;

/**
 * Created by Jake on 10/4/2017.
 */

public class HomeTimelineFragment extends Fragment {

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
                    ((HomeTimelineActivity)getActivity()).populateTimeline(page+1,false);
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
                ((HomeTimelineActivity)getActivity()).populateTimeline(0, true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        mAdapter = new TweetAdapter(getContext());
        mTweetRecycler.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    public void addItems(ArrayList<Tweet> tweets, boolean refresh){
        mAdapter.addTweets(tweets);
        swipeContainer.setRefreshing(false);
    }

    public void addTweet(Tweet tweet) {
        mAdapter.addTweet(tweet);
        mTweetRecycler.scrollToPosition(0);
    }







}
