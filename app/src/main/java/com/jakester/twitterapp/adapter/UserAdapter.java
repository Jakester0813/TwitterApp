package com.jakester.twitterapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.ProfileActivity;
import com.jakester.twitterapp.activities.TweetActivity;
import com.jakester.twitterapp.customwidgets.LinkifiedTextView;
import com.jakester.twitterapp.models.Tweet;
import com.jakester.twitterapp.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by Jake on 9/27/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewholder>{

    Context mContext;
    ArrayList<User> mUsers;

    public UserAdapter(Context context) {
        this.mContext = context;
        this.mUsers = new ArrayList<User>();
    }

    public void addUsers(ArrayList<User> users){
        this.mUsers.addAll(users);
        notifyDataSetChanged();
    }

    public void addUser(User user){
        this.mUsers.add(0,user);
        notifyItemChanged(0,user);
    }

    public User getUser(int position){
        return mUsers.get(position);
    }

    public void clear() {
        this.mUsers.clear();
        notifyDataSetChanged();
    }


    @Override
    public UserViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewholder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewholder = inflater.inflate(R.layout.user_layout, parent, false);
        return new UserViewholder(viewholder);
    }

    @Override
    public void onBindViewHolder(UserViewholder holder, int position) {
        holder.bind(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mProfileImage;
        public TextView mUserName, mUserHandle, mDescription;
        private User user;

        public UserViewholder(View itemView) {
            super(itemView);
            this.mProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            this.mUserName = (TextView) itemView.findViewById(R.id.tv_username);
            this.mUserHandle = (TextView) itemView.findViewById(R.id.tv_user_handle);
            this.mDescription = (TextView) itemView.findViewById(R.id.tv_description);
            itemView.setOnClickListener(this);

        }

        public void bind(User user){
            Glide.with(mContext).load(user.getProfileImage()).into(mProfileImage);
            this.mUserName.setText(user.getName());
            this.mUserHandle.setText(user.getScreenName());
            this.mDescription.setText(user.getDescription());
            this.user = user;
        }

        @Override
        public void onClick(View view) {
            Intent userDetails = new Intent(mContext, ProfileActivity.class);
            userDetails.putExtra("user", Parcels.wrap(user));
            mContext.startActivity(userDetails);

        }
    }
}
