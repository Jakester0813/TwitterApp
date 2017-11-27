package com.jakester.twitterapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.jakester.twitterapp.listener.EndlessScrollListener;
import com.jakester.twitterapp.interfaces.TweetTouchCallback;
import com.jakester.twitterapp.managers.InternetManager;
import com.jakester.twitterapp.models.SimpleDividerItemDecoration;
import com.jakester.twitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jake on 10/4/2017.
 */

public class SearchFragment extends BaseTimelineFragment  implements TweetTouchCallback {
    ProgressBar mProgressBar;
    String mQuery;

    //inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb_tweet_search);
        mRecycler = (RecyclerView) v.findViewById(R.id.rv_tweets);
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mManager);
        mAdapter = new TweetAdapter(getActivity(), this);
        scrollListener = new EndlessScrollListener(mManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(InternetManager.getInstance(getContext()).isInternetAvailable()) {
                    searchTweets(mQuery,mAdapter.getTweet(totalItemsCount-1).getTweetId());
                }
                else{
                    ((HomeTimelineActivity)getActivity()).showNoInternetDialog();
                }
            }
        };
        mRecycler.addOnScrollListener(scrollListener);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                    if(mAdapter != null && mAdapter.getItemCount() > 0) {
                        mAdapter.clear();
                        scrollListener.resetState();
                    }
                    mQuery = query;
                    mRecycler.setVisibility(View.GONE);
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



    public interface NewTweetCallback {
        void onTweetAction(Tweet tweet, boolean reply);
    }

    protected void searchTweets(String q, String maxId) {
        ((HomeTimelineActivity)getActivity()).showProgressBar();
        client.getSearchResult(q, maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ((HomeTimelineActivity)getActivity()).hideProgressBar();
                Log.d("OBJECT", response.toString());
                try {
                    JSONArray obj = response.getJSONArray("statuses");
                    mProgressBar.setVisibility(View.GONE);
                    mRecycler.setVisibility(View.VISIBLE);
                    tweets = Tweet.fromJson(obj);
                    mAdapter.addTweets(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("ARRAY", response.toString());
                ((HomeTimelineActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                ((HomeTimelineActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                ((HomeTimelineActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                ((HomeTimelineActivity)getActivity()).hideProgressBar();
            }
        });
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
}
