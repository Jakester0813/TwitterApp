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

public class MentionsTimelineFragment extends Fragment {

    ArrayList<Tweet> tweets;
    TweetAdapter mAdapter;
    RecyclerView mTweetRecycler;
    LinearLayoutManager mManager;
    private TwitterClient client;

    //inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        mTweetRecycler = (RecyclerView) v.findViewById(R.id.rv_tweets);
        mManager = new LinearLayoutManager(getContext());
        mTweetRecycler.setLayoutManager(mManager);

        mAdapter = new TweetAdapter(getContext());
        mTweetRecycler.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeTimelineActivity)getActivity()).populateMentionsTimeline();
    }

    public void addItems(ArrayList<Tweet> tweets){
        mAdapter.addTweets(tweets);
    }

}
