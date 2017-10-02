package com.jakester.twitterapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.models.Tweet;

import org.parceler.Parcels;

import de.hdodenhof.circleimageview.CircleImageView;

public class TweetActivity extends AppCompatActivity {

    TextView mUserName, mUserHandle, mTweetBody, mTimeStamp, mNumOfLikes, mLikesText;
    CircleImageView mProfileImage;
    Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        mProfileImage = (CircleImageView) findViewById(R.id.iv_tweet_profile_image);
        mUserName = (TextView) findViewById(R.id.tv_tweet_username);
        mUserHandle = (TextView) findViewById(R.id.tv_tweet_userhandle);
        mTweetBody = (TextView) findViewById(R.id.tv_tweet_body);
        mTimeStamp = (TextView) findViewById(R.id.tv_tweet_time);
        mNumOfLikes = (TextView) findViewById(R.id.tv_num_likes);
        mLikesText = (TextView) findViewById(R.id.tv_likes_text);


        mTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Glide.with(this).load(mTweet.getUserImageUrl()).into(mProfileImage);
        mUserName.setText(mTweet.getUserName());
        mUserHandle.setText(mTweet.getUserHandle());
        mTweetBody.setText(mTweet.getTweet());
        mTimeStamp.setText(mTweet.getTimestampDetail());
        mNumOfLikes.setText(Integer.toString(mTweet.getFavoritedCount()));

    }
}
