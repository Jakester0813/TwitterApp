package com.jakester.twitterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.HomeTimelineActivity;
import com.jakester.twitterapp.activities.TweetActivity;
import com.jakester.twitterapp.adapter.TweetAdapter;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.listener.TweetTouchCallback;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.Tweet;
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

public class MessageFragment extends Fragment  implements TweetTouchCallback {

    ArrayList<Tweet> tweets;
    TweetAdapter mAdapter;
    RecyclerView mTweetRecycler;
    ProgressBar mProgressBar;

    private EndlessScrollListener scrollListener;
    TwitterClient mClient;

    //inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb_tweet_search);
        mTweetRecycler = (RecyclerView) v.findViewById(R.id.rv_tweets);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mTweetRecycler.setLayoutManager(mManager);
        mAdapter = new TweetAdapter(getActivity(), this);


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(InternetManager.getInstance(getActivity()).isInternetAvailable()) {
                    if(mAdapter != null) {
                        mAdapter.clear();
                        scrollListener.resetState();
                    }
                    mTweetRecycler.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    searchTweets(query, "");
                    searchView.clearFocus();
                }
                else{
                    //noInternetDialog.show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void searchTweets(String q, String maxId) {
        mClient.getSearchResult(q, maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                mProgressBar.setVisibility(View.GONE);
                mTweetRecycler.setVisibility(View.VISIBLE);
                tweets = Tweet.fromJson(response);
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

    public interface NewTweetCallback {
        void onTweetAction(Tweet tweet, boolean reply);
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mClient = TwitterApplication.getRestClient();
    }


    @Override
    public void onClick(View view, Tweet tweet) {
        if(view.getId() == R.id.iv_favorite) {
            favoriteTweet(tweet);
        }
        else if (view.getId() == R.id.iv_retweet) {
            retweetTweet(tweet);
        }
        else if(view.getId() == R.id.iv_reply) {
            ((HomeTimelineActivity) getActivity()).showNewTweetDialog(true, tweet.getUser());
        }
        else{
            Intent tweetDetails = new Intent(getActivity(), TweetActivity.class);
            tweetDetails.putExtra("tweet", Parcels.wrap(tweet));
            startActivityForResult(tweetDetails, 1);
        }
    }

    private void favoriteTweet(final Tweet tweet){
        mClient.favoriteTweet(tweet.getFavorited(), tweet.getTweetId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("OBJECT", response.toString());
                tweet.setFavorited(!tweet.getFavorited());
                tweet.setFavoritedCount(tweet.getFavorited());
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
}
