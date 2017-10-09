package com.jakester.twitterapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Jake on 9/28/2017.
 */
@Parcel(analyze={User.class})
public class User {

    public String name;
    public long mUserId;
    public String screenName;
    public String description;
    public String location;
    public String profileImageURL;
    public String profileBackgroundImageUrl;
    public String profileBackgroundColor;
    public int following;
    public int followers;

    public static User fromJSON(JSONObject json) throws JSONException{
        User user = new User();

        user.name = json.getString("name");
        user.mUserId = json.getLong("id");
        user.screenName = "@" + json.getString("screen_name");
        user.location = json.getString("location");
        user.description = json.getString("description");
        user.profileImageURL = json.getString("profile_image_url");
        user.profileBackgroundImageUrl = json.optString("profile_banner_url");
        user.profileBackgroundColor = "#" + json.getString("profile_link_color");
        user.following = json.getInt("friends_count");
        user.followers = json.getInt("followers_count");

        return user;
    }

    public static ArrayList<User> fromJson(JSONArray jsonArray) {
        ArrayList<User> users = new ArrayList<User>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject userJson = null;
            try {
                userJson = jsonArray.getJSONObject(i);
                User user = fromJSON(userJson);
                users.add(user);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }


        }

        return users;
    }

    public long getUserId() { return mUserId; }

    public String getName(){
        return name;
    }

    public String getScreenName(){
        return screenName;
    }

    public String getLocation() { return location; }

    public String getDescription() { return description; }

    public String getProfileImage(){
        return profileImageURL;
    }

    public String getBannerImage(){
        return profileBackgroundImageUrl;
    }

    public String getBackgroundColor() { return profileBackgroundColor; }

    public int getFollowing() { return following; }

    public int getFollowers() { return followers; }
}
