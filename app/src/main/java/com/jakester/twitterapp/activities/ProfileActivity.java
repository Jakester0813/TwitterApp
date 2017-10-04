package com.jakester.twitterapp.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.network.TwitterClient;
import com.jakester.twitterapp.util.TwitterContstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView mProfileImage;
    ImageView mBackgroundImage;
    TextView mUserName, mUserHandle, mDescription, mLocation, mNumFollowing, mFollowingText,
            mNumFollowers, mFollowersText;
    ProgressBar mProgress;
    RecyclerView mTweetsRecycler;
    LinearLayoutManager mLayoutManager;
    TweetAdapter mAdapter;
    TwitterClient mClient;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        mClient = TwitterApplication.getRestClient();
        mProfileImage = (CircleImageView) findViewById(R.id.cv_profile_user_image);
        mBackgroundImage = (ImageView) findViewById(R.id.iv_profile_banner);
        mUserName = (TextView) findViewById(R.id.tv_profile_username);
        mUserHandle = (TextView) findViewById(R.id.tv_profile_handle);
        mDescription = (TextView) findViewById(R.id.tv_profile_description);
        mLocation = (TextView) findViewById(R.id.tv_profile_location);
        mNumFollowing = (TextView) findViewById(R.id.tv_followers_num);
        mFollowingText = (TextView) findViewById(R.id.tv_followers_text);
        mNumFollowers = (TextView) findViewById(R.id.tv_following_num);
        mFollowersText = (TextView) findViewById(R.id.tv_following_text);

        Glide.with(this).load(mUser.getProfileImage()).into(mProfileImage);
        if(!mUser.getBannerImage().equals(TwitterContstants.DEFAULT_BANNER_URL)) {
            Glide.with(this).load(mUser.getBannerImage()).into(mBackgroundImage);
        }
        else{
            mBackgroundImage.setBackgroundColor(Color.parseColor(mUser.getBackgroundColor()));
        }
        mUserName.setText(mUser.getName());
        mUserHandle.setText(mUser.getScreenName());
        mDescription.setText(mUser.getDescription());
        mLocation.setText(mUser.getLocation());
        mNumFollowing.setText(Integer.toString(mUser.getFollowing()));
        mNumFollowers.setText(Integer.toString(mUser.getFollowing()));

        mProgress = (ProgressBar) findViewById(R.id.pb_tweet);
        mTweetsRecycler = (RecyclerView) findViewById(R.id.rv_user_tweets);
        mLayoutManager = new LinearLayoutManager(this);
        mTweetsRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new TweetAdapter(this);
        mTweetsRecycler.setAdapter(mAdapter);

        loadUserTweets();
    }

    public void loadUserTweets(){
        mClient.getUserTimeline(Long.toString(mUser.getUserId()), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mAdapter.addTweets(Tweet.fromJson(response));
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }
        });
    }


}
