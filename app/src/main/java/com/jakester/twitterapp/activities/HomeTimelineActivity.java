package com.jakester.twitterapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.scribejava.apis.TwitterApi;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.fragments.NewTweetDialogFragment;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.network.TwitterClient;
import com.jakester.twitterapp.util.TwitterContstants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

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

public class HomeTimelineActivity extends AppCompatActivity implements NewTweetDialogFragment.FilterDialogListener{

    private Subscription subscription;
    ArrayList<Tweet> tweets;
    TweetAdapter mAdapter;
    RecyclerView mTweetRecycler;
    LinearLayoutManager mManager;
    private SwipeRefreshLayout swipeContainer;

    private EndlessScrollListener scrollListener;
    CircleImageView mProfilePhoto;
    AlertDialog noInternetDialog;
    private TwitterClient client;
    User currentUser;
    boolean started;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        started = true;
        mProfilePhoto = (CircleImageView) findViewById(R.id.iv_profile_image);
        mTweetRecycler = (RecyclerView) findViewById(R.id.rv_tweets);
        mManager = new LinearLayoutManager(this);
        mTweetRecycler.setLayoutManager(mManager);
        scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(HomeTimelineActivity.this).isInternetAvailable()) {
                    populateTimeline(page+1, false);
                }
                else{
                    noInternetDialog.show();
                }
            }
        };
        mTweetRecycler.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srl_swipe_container);
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewTweetDialog();
            }
        });

        mAdapter = new TweetAdapter(HomeTimelineActivity.this);
        mTweetRecycler.setAdapter(mAdapter);

        client = TwitterApplication.getRestClient();

        noInternetDialog = InternetManager.getInstance(this).noInternetDialog();

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeTimelineActivity.this, ProfileActivity.class);
                i.putExtra("user", Parcels.wrap(currentUser));
                startActivity(i);
            }
        });

        /*
        TwitterClient client = TwitterApplication.getRestClient();
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // Response is automatically parsed into a JSONArray
                tweets = Tweet.fromJson(json);


            }
        });

        /*
        subscription = getIntObservable()
                //
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

            //Called when the observable is done, called only once
            //Called right after onNext()
            @Override
            public void onCompleted() {
            }

            //Called when an error occurs
            @Override
            public void onError(Throwable e) {
                Log.e("GMAIL",e.getMessage(), e);
            }

            //This gets called when the API call gets returned and the int is returned
            @Override
            public void onNext(Integer integer) {
                Log.d("I DID IT", integer.toString());
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(client.isAuthenticated() && InternetManager.getInstance(this).isInternetAvailable()) {
            getUser();
            populateTimeline(0, false);
        }
        if(!InternetManager.getInstance(this).isInternetAvailable()){
            ArrayList<Tweet> tweets = (ArrayList<Tweet>) SQLite.select()
                    .from(Tweet.class).queryList();
            mAdapter.addTweets(tweets);
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //This is essential as it prevents a memory leak
        //if(subscription != null && !subscription.isUnsubscribed()){
        //    subscription.unsubscribe();
        //}
    }

    public void populateTimeline(int page, final boolean refresh){
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> tweets =  Tweet.fromJson(response);
                if(refresh){
                    mAdapter.clear();
                }
                mAdapter.addTweets(tweets);
                swipeContainer.setRefreshing(false);

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

    public void getUser(){
        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    currentUser = User.fromJSON(response);
                    Glide.with(HomeTimelineActivity.this).load(currentUser.profileImageURL).into(mProfilePhoto);
                    mProfilePhoto.setVisibility(View.VISIBLE);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
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

    private void showNewTweetDialog() {
        FragmentManager fm = this.getSupportFragmentManager();
        NewTweetDialogFragment filterDialog = NewTweetDialogFragment.newInstance(TwitterContstants.COMPOSE_TWEET);
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(currentUser));
        filterDialog.setArguments(args);
        filterDialog.show(fm,TwitterContstants.FRAGMENT_TWEET);
    }

    @Override
    public void onFinishFilterDialog(Tweet tweet) {
        client.postTweet(tweet.getTweet(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
        mAdapter.addTweet(tweet);
        mTweetRecycler.scrollToPosition(0);
    }

    //Can incorporate Models into this, maybe as a way to incorporate Retrofit?
    public Observable<Integer> getIntObservable(){
        return Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.just(1);
            }
        });
    }
}
