package com.jakester.twitterapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.adapter.TwitterFragmentPagerAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.fragments.HomeTimelineFragment;
import com.jakester.twitterapp.fragments.NewTweetDialogFragment;
import com.jakester.twitterapp.fragments.MentionsTimelineFragment;
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

public class HomeTimelineActivity extends AppCompatActivity{

    private Subscription subscription;

    MenuItem miActionProgressItem;
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

        client = TwitterApplication.getRestClient();
        started = true;
        mProfilePhoto = (CircleImageView) findViewById(R.id.iv_profile_image);


        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new TwitterFragmentPagerAdapter(getSupportFragmentManager(), this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

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

    @Override
    protected void onResume() {
        super.onResume();
        if(client.isAuthenticated() && InternetManager.getInstance(this).isInternetAvailable()) {
            getUser();
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

    public void showNewTweetDialog() {
        FragmentManager fm = this.getSupportFragmentManager();
        NewTweetDialogFragment filterDialog = NewTweetDialogFragment.newInstance(TwitterContstants.COMPOSE_TWEET);
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(currentUser));
        filterDialog.setArguments(args);
        filterDialog.show(fm,TwitterContstants.FRAGMENT_TWEET);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timeline_menu, menu);
        return true;
    }

    public void showProgressBar() {
        // Show progress item
        if(miActionProgressItem != null)
            miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        if(miActionProgressItem != null)
            miActionProgressItem.setVisible(false);
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


    public void showNoInternetDialog(){
        noInternetDialog.show();
    }

}
