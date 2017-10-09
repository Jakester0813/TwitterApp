package com.jakester.twitterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.HomeTimelineActivity;
import com.jakester.twitterapp.activities.TweetActivity;
import com.jakester.twitterapp.adapter.MessageAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.listener.TweetTouchCallback;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Message;
import com.jakester.twitterapp.models.SimpleDividerItemDecoration;
import com.jakester.twitterapp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jake on 10/4/2017.
 */

public class MessageFragment extends Fragment{

    private EndlessScrollListener scrollListener;
    ArrayList<Message> messages;
    MessageAdapter mAdapter;
    RecyclerView mMessagesRecycler;
    LinearLayoutManager mManager;
    TwitterClient mClient;

    //inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mMessagesRecycler = (RecyclerView) v.findViewById(R.id.rv_messages);
        mManager = new LinearLayoutManager(getActivity());
        mMessagesRecycler.setLayoutManager(mManager);
        mAdapter = new MessageAdapter(getActivity());
        mMessagesRecycler.setAdapter(mAdapter);
        mMessagesRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getMessages(String maxId) {
        mClient.getDirectMessages(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                mMessagesRecycler.setVisibility(View.VISIBLE);
                messages = Message.fromJson(response);
                mAdapter.addMessages(messages);
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
        mClient = TwitterApplication.getRestClient();
        scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(getContext()).isInternetAvailable()) {
                    getMessages(mAdapter.getMessage(totalItemsCount-1).getMessageId());
                }
                else{
                    ((HomeTimelineActivity)getActivity()).showNoInternetDialog();
                }
            }
        };
        mMessagesRecycler.addOnScrollListener(scrollListener);

        getMessages("");
    }


}
