package com.jakester.twitterapp.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by Jake on 9/28/2017.
 */
@Parcel
public class User {

    public String name;
    public long mUserId;
    public String screenName;
    public String profileImageURL;

    public static User fromJSON(JSONObject json) throws JSONException{
        User user = new User();

        user.name = json.getString("name");
        user.mUserId = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageURL = json.getString("profile_image_url");

        return user;
    }

    public String getName(){
        return name;
    }

    public String getScreenName(){
        return screenName;
    }

    public String getProfileImage(){
        return profileImageURL;
    }
}
