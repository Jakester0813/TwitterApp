package com.jakester.twitterapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.ProfileActivity;
import com.jakester.twitterapp.activities.TweetActivity;
import com.jakester.twitterapp.customwidgets.LinkifiedTextView;
import com.jakester.twitterapp.listener.TweetTouchCallback;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.util.PatternEditableBuilder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Jake on 9/27/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewholder>{

    Context mContext;
    ArrayList<Tweet> mTweets;
    TweetTouchCallback mCallback;

    public TweetAdapter(Context context, TweetTouchCallback callback) {
        this.mContext = context;
        this.mTweets = new ArrayList<Tweet>();
        this.mCallback = callback;
    }

    public void addTweets(ArrayList<Tweet> tweets){
        this.mTweets.addAll(tweets);
        notifyDataSetChanged();
    }

    public void addTweet(Tweet tweet){
        this.mTweets.add(0,tweet);
        notifyItemChanged(0,tweet);
    }

    public Tweet getTweet(int position){
        return mTweets.get(position);
    }

    public void clear() {
        this.mTweets.clear();
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

    public class TweetViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mProfileImage, mReplyImage, mRetweetImage, mFavoriteImage;
        public TextView mRetweetedBy, mUserName, mUserHandle, mTimeStamp, mRetweetsNum, mFavoritesNum;
        public LinkifiedTextView mBody;
        public Tweet mTweet;

        public TweetViewholder(View itemView) {
            super(itemView);
            this.mRetweetedBy = (TextView) itemView.findViewById(R.id.tv_replying_to);
            this.mProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            this.mUserName = (TextView) itemView.findViewById(R.id.tv_username);
            this.mUserHandle = (TextView) itemView.findViewById(R.id.tv_user_handle);
            this.mTimeStamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            this.mBody = (LinkifiedTextView) itemView.findViewById(R.id.tv_tweet_body);
            this.mReplyImage = (ImageView) itemView.findViewById(R.id.iv_reply);
            this.mRetweetImage = (ImageView) itemView.findViewById(R.id.iv_retweet);
            this.mRetweetsNum = (TextView) itemView.findViewById(R.id.tv_retweet_num);
            this.mFavoriteImage = (ImageView) itemView.findViewById(R.id.iv_favorite);
            this.mFavoritesNum = (TextView) itemView.findViewById(R.id.tv_favorite_num);
            this.mProfileImage.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void bind(Tweet tweet){
            if(!tweet.getRetweetedBy().equals("")) {
                this.mRetweetedBy.setText(tweet.getRetweetedBy());
                this.mRetweetedBy.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext).load(tweet.getUserImageUrl()).into(mProfileImage);
            this.mUserName.setText(tweet.getUserName());
            this.mUserHandle.setText(tweet.getUserHandle());
            this.mTimeStamp.setText(tweet.getTimestamp());
            this.mBody.setText(tweet.getTweet());
            this.mTweet = tweet;
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\@(\\w+)"), mContext.getResources().getColor(R.color.colorPrimary),
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String text) {
                                    Toast.makeText(mContext, "USER!!: " + text,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).into(this.mBody);
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\#(\\w+)"), mContext.getResources().getColor(R.color.colorPrimary),
                            new PatternEditableBuilder.SpannableClickedListener() {
                                @Override
                                public void onSpanClicked(String text) {
                                    Toast.makeText(mContext, "Hashtag!!: " + text,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).into(this.mBody);
            this.mReplyImage.setOnClickListener(this);
            this.mRetweetImage.setOnClickListener(this);
            this.mRetweetImage.setImageResource(tweet.getRetweeted() ? R.drawable.ic_retweeted : R.drawable.ic_retweet);
            if(tweet.getRetweetCount() > 0){
                this.mRetweetsNum.setText(Integer.toString(tweet.getRetweetCount()));
            }
            this.mFavoriteImage.setImageResource(tweet.getFavorited() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            this.mFavoriteImage.setOnClickListener(this);
            if(tweet.getFavoritedCount() > 0){
                this.mFavoritesNum.setText(Integer.toString(tweet.getFavoritedCount()));
            }


        }

        @Override
        public void onClick(View view) {
            if(view.getId() != R.id.tv_tweet_body) {
                if (view.getId() == R.id.iv_profile_image) {
                    Intent userDetails = new Intent(mContext, ProfileActivity.class);
                    userDetails.putExtra("user", Parcels.wrap(mTweet.getUser()));
                    mContext.startActivity(userDetails);
                } else {
                    if (view.getId() == R.id.iv_favorite) {
                        this.mFavoriteImage.setImageResource(mTweet.getFavorited() ?
                                R.drawable.ic_favorite_border : R.drawable.ic_favorite);
                        this.mFavoritesNum.setText(mTweet.getFavorited() ? Integer.toString(mTweet.getFavoritedCount() - 1)
                                : Integer.toString(mTweet.getFavoritedCount() + 1));
                    } else if (view.getId() == R.id.iv_retweet) {
                        this.mRetweetImage.setImageResource(mTweet.getRetweeted() ?
                                R.drawable.ic_retweet : R.drawable.ic_retweeted);
                        this.mRetweetsNum.setText(mTweet.getRetweeted() ? Integer.toString(mTweet.getRetweetCount() - 1)
                                : Integer.toString(mTweet.getRetweetCount() + 1));
                    }
                    mCallback.onClick(view, mTweet);
                }
            }

        }
    }
}
