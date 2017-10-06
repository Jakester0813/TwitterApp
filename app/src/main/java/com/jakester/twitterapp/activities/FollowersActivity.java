package com.jakester.twitterapp.activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jakester.twitterapp.R;
import com.jakester.twitterapp.adapter.UserAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FollowersActivity extends AppCompatActivity{

    ArrayList<Tweet> tweets;
    UserAdapter mAdapter;
    RecyclerView mTweetRecycler;
    LinearLayoutManager mManager;

    AlertDialog noInternetDialog;
    private TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_follows);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Following");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);
        noInternetDialog = InternetManager.getInstance(this).noInternetDialog();
        final String id = getIntent().getStringExtra("id");
        mTweetRecycler = (RecyclerView) findViewById(R.id.rv_follows);
        mManager = new LinearLayoutManager(this);
        mTweetRecycler.setLayoutManager(mManager);

        mAdapter = new UserAdapter(this);
        mTweetRecycler.setAdapter(mAdapter);
        EndlessScrollListener scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(FollowersActivity.this).isInternetAvailable()) {
                    getFollowers(id, page);
                }
            }
        };
        mTweetRecycler.addOnScrollListener(scrollListener);

        client = TwitterApplication.getRestClient();

        getFollowers(id, 0);
    }

    public void getFollowers(String id, int page){
        client.getFollowers(id, page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray users = response.getJSONArray("users");
                    mAdapter.addUsers(User.fromJson(users));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("OBJECT", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void showNoInternetDialog(){
        noInternetDialog.show();
    }

}
