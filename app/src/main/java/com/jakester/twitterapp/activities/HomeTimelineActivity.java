package com.jakester.twitterapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.scribejava.apis.TwitterApi;
import com.jakester.twitterapp.R;
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
import rx.Observable;
import rx.Subscription;
import rx.functions.Func0;

public class HomeTimelineActivity extends AppCompatActivity {

    private Subscription subscription;
    ArrayList<Tweet> tweets;
    TweetAdapter mAdapter;
    RecyclerView mTweetRecycler;
    LinearLayoutManager mManager;
    private EndlessScrollListener scrollListener;
    AlertDialog noInternetDialog;
    private TwitterClient client;
    boolean started;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        started = true;

        mTweetRecycler = (RecyclerView) findViewById(R.id.rv_tweets);
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mTweetRecycler.setLayoutManager(mManager);
        scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(HomeTimelineActivity.this).isInternetAvailable()) {
                    populateTimeline(page+1);
                }
                else{
                    noInternetDialog.show();
                }
            }
        };
        mTweetRecycler.addOnScrollListener(scrollListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAdapter = new TweetAdapter(HomeTimelineActivity.this);
        mTweetRecycler.setAdapter(mAdapter);

        client = TwitterApplication.getRestClient();

        noInternetDialog = InternetManager.getInstance(this).noInternetDialog();


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
            populateTimeline(0);
        }
        if(!InternetManager.getInstance(this).isInternetAvailable()){
            noInternetDialog.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_twitter, menu);
        MenuItem logIn = menu.findItem(R.id.action_login);
        if(client.isAuthenticated()){
            logIn.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void populateTimeline(int page){
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> tweets =  Tweet.fromJson(response);
                mAdapter.addTweets(tweets);
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
