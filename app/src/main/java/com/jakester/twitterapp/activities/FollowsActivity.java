package com.jakester.twitterapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.adapter.TwitterFragmentPagerAdapter;
import com.jakester.twitterapp.adapter.UserAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.fragments.NewTweetDialogFragment;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.SimpleDividerItemDecoration;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.network.TwitterClient;
import com.jakester.twitterapp.util.TwitterContstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func0;

public class FollowsActivity extends AppCompatActivity{

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
        toolbar.setTitle("Followers");
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
        mTweetRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
        EndlessScrollListener scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(FollowsActivity.this).isInternetAvailable()) {
                    getFollows(id, page);
                }
            }
        };
        mTweetRecycler.addOnScrollListener(scrollListener);

        client = TwitterApplication.getRestClient();

        getFollows(id, 0);
    }

    public void getFollows(String id, int page){
        client.getFollowing(id, page, new JsonHttpResponseHandler() {
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
