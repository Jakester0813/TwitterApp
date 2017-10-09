package com.jakester.twitterapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.activities.ProfileActivity;
import com.jakester.twitterapp.models.Message;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by Jake on 9/27/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewholder>{

    Context mContext;
    ArrayList<Message> mMessages;

    public MessageAdapter(Context context) {
        this.mContext = context;
        this.mMessages = new ArrayList<Message>();
    }

    public void addMessages(ArrayList<Message> messages){
        this.mMessages.addAll(messages);
        notifyDataSetChanged();
    }

    public void addMessage(Message message){
        this.mMessages.add(0,message);
        notifyItemChanged(0,message);
    }

    public Message getMessage(int position){
        return mMessages.get(position);
    }

    public void clear() {
        this.mMessages.clear();
        notifyDataSetChanged();
    }


    @Override
    public MessageViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewholder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewholder = inflater.inflate(R.layout.message_layout, parent, false);
        return new MessageViewholder(viewholder);
    }

    @Override
    public void onBindViewHolder(MessageViewholder holder, int position) {
        holder.bind(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class MessageViewholder extends RecyclerView.ViewHolder{

        public ImageView mProfileImage;
        public TextView mUserName, mUserHandle, mMessage, mTimeStamp;

        public MessageViewholder(View itemView) {
            super(itemView);
            this.mProfileImage = (ImageView) itemView.findViewById(R.id.iv_profile_image);
            this.mUserName = (TextView) itemView.findViewById(R.id.tv_username);
            this.mUserHandle = (TextView) itemView.findViewById(R.id.tv_user_handle);
            this.mMessage = (TextView) itemView.findViewById(R.id.tv_message);
            this.mTimeStamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);

        }

        public void bind(Message message){
            Glide.with(mContext).load(message.getProfileImageUrl()).into(mProfileImage);
            this.mUserName.setText(message.getName());
            this.mUserHandle.setText(message.getScreenName());
            this.mMessage.setText(message.getMessage());
            this.mTimeStamp.setText(message.getTimeStamp());
        }

    }
}
