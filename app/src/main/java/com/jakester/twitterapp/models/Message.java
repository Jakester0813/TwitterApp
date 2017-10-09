package com.jakester.twitterapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Jake on 9/28/2017.
 */
@Parcel(analyze={Message.class})
public class Message {


    public String mMessageId;
    public String mName;
    public String mScreenName;
    public String mProfileImageURL;
    public String mMessage;
    public String mTimeStamp;

    public static Message fromJSON(JSONObject json) throws JSONException{
        Message message = new Message();

        message.mMessageId = json.getString("id_str");
        message.mMessage = json.getString("text");
        String[] str = json.getString("created_at").split(" ");
        message.mTimeStamp = str[2] + " " + str[1];
        JSONObject sender = json.getJSONObject("sender");
        message.mName = sender.getString("name");
        message.mScreenName = "@" + sender.getString("screen_name");
        message.mProfileImageURL = sender.getString("profile_image_url");

        return message;
    }

    public static ArrayList<Message> fromJson(JSONArray jsonArray) {
        ArrayList<Message> users = new ArrayList<Message>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject userJson = null;
            try {
                userJson = jsonArray.getJSONObject(i);
                Message user = fromJSON(userJson);
                users.add(user);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }


        }

        return users;
    }

    public String getMessageId() { return mMessageId; }

    public String getName(){
        return mName;
    }

    public String getScreenName(){
        return mScreenName;
    }

    public String getProfileImageUrl() { return mProfileImageURL; }

    public String getMessage() { return mMessage; }

    public String getTimeStamp(){
        return mTimeStamp;
    }

}
