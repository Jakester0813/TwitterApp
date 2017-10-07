package com.jakester.twitterapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.fragments.NewTweetDialogFragment;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.network.TwitterClient;
import com.jakester.twitterapp.util.PatternEditableBuilder;
import com.jakester.twitterapp.util.TwitterContstants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.id.message;

public class TweetActivity extends AppCompatActivity implements NewTweetDialogFragment.FilterDialogListener {

    TextView mUserName, mUserHandle, mTweetBody, mTimeStamp, mNumOfLikes, mNumOfRetweets;
    ImageView mReplyImage, mRetweetImage, mLikesImage;
    CircleImageView mProfileImage;
    Tweet mTweet;
    TwitterClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().on
        mProfileImage = (CircleImageView) findViewById(R.id.iv_tweet_profile_image);
        mUserName = (TextView) findViewById(R.id.tv_tweet_username);
        mUserHandle = (TextView) findViewById(R.id.tv_tweet_userhandle);
        mTweetBody = (TextView) findViewById(R.id.tv_tweet_body);
        mTimeStamp = (TextView) findViewById(R.id.tv_tweet_time);
        mReplyImage = (ImageView) findViewById(R.id.iv_reply);
        mRetweetImage = (ImageView) findViewById(R.id.iv_retweet);
        mNumOfRetweets = (TextView) findViewById(R.id.tv_retweet_num);
        mLikesImage = (ImageView) findViewById(R.id.iv_favorite);
        mNumOfLikes = (TextView) findViewById(R.id.tv_favorite_num);


        mTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Glide.with(this).load(mTweet.getUserImageUrl()).into(mProfileImage);
        mUserName.setText(mTweet.getUserName());
        mUserHandle.setText(mTweet.getUserHandle());
        mTweetBody.setText(mTweet.getTweet());
        mTimeStamp.setText(mTweet.getTimestampDetail());
        mRetweetImage.setImageResource(mTweet.getRetweeted() ? R.drawable.ic_retweeted : R.drawable.ic_retweet);
        mNumOfRetweets.setText(Integer.toString(mTweet.getRetweetCount()));
        mLikesImage.setImageResource(mTweet.getFavorited() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        mNumOfLikes.setText(Integer.toString(mTweet.getFavoritedCount()));


        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(TweetActivity.this, "USER!!: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(this.mTweetBody);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(TweetActivity.this, "Hashtag!!: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(this.mTweetBody);

        mClient = TwitterApplication.getRestClient();

        mLikesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteTweet(mTweet);
            }
        });

        mRetweetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retweetTweet(mTweet);
            }
        });

        mReplyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUser(mTweet.getUser());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void favoriteTweet(final Tweet tweet){
        mClient.favoriteTweet(tweet.getFavorited(), tweet.getTweetId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                tweet.setFavorited(!tweet.getFavorited());
                tweet.setFavoritedCount(tweet.getFavorited());
                mLikesImage.setImageResource(mTweet.getFavorited() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                tweet.save();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                //addTweet(tweet);
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

    public void postTweet(final Tweet tweet, boolean reply) {
        mClient.postTweet(tweet.getTweet(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                //addTweet(tweet);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                //addTweet(tweet);
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

    private void retweetTweet(final Tweet tweet){
        mClient.reTweet(tweet.getRetweeted(), tweet.getTweetId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                tweet.setRetweeted(!tweet.getRetweeted());
                tweet.setFavoritedCount(tweet.getRetweeted());
                mRetweetImage.setImageResource(mTweet.getRetweeted() ? R.drawable.ic_retweeted : R.drawable.ic_retweet);
                tweet.save();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                //addTweet(tweet);
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
    public void onFinishFilterDialog(Tweet tweet, boolean reply) {
        Intent intent=new Intent();
        intent.putExtra("tweet",Parcels.wrap(tweet));
        setResult(5,intent);
        finish();
    }

    public void showNewTweetDialog(User mCurrentUser, User user){
        FragmentManager fm =  getSupportFragmentManager();
        NewTweetDialogFragment filterDialog = NewTweetDialogFragment.newInstance(TwitterContstants.COMPOSE_TWEET);
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(mCurrentUser));
        args.putParcelable("user_reply", Parcels.wrap(user));

        args.putBoolean("reply", true);
        filterDialog.setArguments(args);
        filterDialog.show(fm,TwitterContstants.FRAGMENT_TWEET);
    }

    public void getUser(final User user){
        mClient.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    User mCurrentUser = User.fromJSON(response);
                    showNewTweetDialog(mCurrentUser, user);
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
}
