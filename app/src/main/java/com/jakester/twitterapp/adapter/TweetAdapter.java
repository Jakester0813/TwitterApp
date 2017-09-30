package com.jakester.twitterapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.models.Tweet;

import java.util.ArrayList;

/**
 * Created by Jake on 9/27/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewholder>{

    Context mContext;
    ArrayList<Tweet> mTweets;

    public TweetAdapter(Context context) {
        this.mContext = context;
        this.mTweets = new ArrayList<Tweet>();
    }

    public void addTweets(ArrayList<Tweet> tweets){
        this.mTweets.addAll(tweets);
        notifyDataSetChanged();
    }

    @Override
    public TweetViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewholder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewholder = inflater.inflate(R.layout.tweet_layout, parent, false);
        return new TweetViewholder(viewholder);
    }

    @Override
    public void onBindViewHolder(TweetViewholder holder, int position) {
        holder.bind(mTweets.get(position));
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class TweetViewholder extends RecyclerView.ViewHolder {

        public ImageView mProfileImage;
        public TextView mUserName, mUserHandle, mTimeStamp, mBody;

        public TweetViewholder(View itemView) {
            super(itemView);
            this.mProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            this.mUserName = (TextView) itemView.findViewById(R.id.tv_username);
            this.mUserHandle = (TextView) itemView.findViewById(R.id.tv_user_handle);
            this.mTimeStamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            this.mBody = (TextView) itemView.findViewById(R.id.tv_tweet_body);
        }

        public void bind(Tweet tweet){
            Glide.with(mContext).load(tweet.getUserImageUrl()).into(mProfileImage);
            this.mUserName.setText(tweet.getUserName());
            this.mUserHandle.setText(tweet.getUserHandle());
            this.mTimeStamp.setText(tweet.getTimestamp());
            this.mBody.setText(tweet.getTweet());
        }

    }
}
