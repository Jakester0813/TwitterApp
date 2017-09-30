package com.jakester.twitterapp.models;

import com.jakester.twitterapp.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/codepath/android_guides/wiki/DBFlow-Guide
 *
 * Note: All models **must extend from** `BaseModel` as shown below.
 * 
 */
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {

	@PrimaryKey
	@Column
	Long id;
	// Define table fields
	@Column
	String timestamp;
	@Column
	String userImage;
	@Column
	String userName;
	@Column
	String userHandle;
	@Column
	String body;
	@Column
	String url;
	@Column
	String expandedUrl;
	@Column
	String displayUrl;
	@Column
	int retweetCount;
	@Column
	int favoritedCount;
	@Column
	boolean favorited;
	@Column
	boolean retweeted;



	public Tweet() {
		super();
	}

	// Add a constructor that creates an object from the JSON response
	public Tweet(JSONObject object){
		super();

		try {
			this.timestamp = object.getString("created_at");
			User user = User.fromJSON(object.getJSONObject("user"));
			this.userImage = user.getProfileImage();
			this.userName = user.getName();
			this.userHandle = user.getScreenName();
			this.body = object.getString("text");
			if(object.optJSONObject("entities").optJSONArray("urls").optJSONObject(0) != null) {
				this.url = object.optJSONObject("entities").optJSONArray("urls").optJSONObject(0).optString("url");
				this.displayUrl = object.optJSONObject("entities").optJSONArray("urls").optJSONObject(0).optString("display_url");
				this.expandedUrl = object.optJSONObject("entities").optJSONArray("urls").optJSONObject(0).optString("expanded_url");
			}
			this.favorited = object.getBoolean("favorited");
			this.favoritedCount = object.getInt("favorite_count");
			this.retweeted = object.getBoolean("retweeted");
			this.retweetCount = object.getInt("retweet_count");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

		for (int i=0; i < jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			Tweet tweet = new Tweet(tweetJson);
			tweet.save();
			tweets.add(tweet);
		}

		return tweets;
	}

	public String getTimestamp(){
		return timestamp;
	}

	public String getUserImageUrl(){
		return userImage;
	}

	public String getUserName(){
		return userName;
	}

	public String getUserHandle(){
		return "@"+userHandle;
	}

	public String getTweet(){
		return body;
	}

	public String getUrl(){
		return url;
	}

	public String getExpandedUrl(){
		return expandedUrl;
	}

	public String getDisplayUrl(){
		return displayUrl;
	}
}
