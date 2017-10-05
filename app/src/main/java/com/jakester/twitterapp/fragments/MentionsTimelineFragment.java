package com.jakester.twitterapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

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
        client = TwitterApplication.getRestClient();
        populateMentionsTimeline();
    }

    public void addItems(ArrayList<Tweet> tweets){
        mAdapter.addTweets(tweets);
    }

    public void populateMentionsTimeline(){
        ((HomeTimelineActivity)getActivity()).showProgressBar();
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ((HomeTimelineActivity)getActivity()).hideProgressBar();
                addItems(Tweet.fromJson(response));

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
