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
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@Parcel
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
			try {
				this.timestamp = calculateTimeStamp(object.getString("created_at"));
			}
			catch (ParseException e) {
				e.printStackTrace();
				this.timestamp = "I AM ERROR";
			}
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

	public String calculateTimeStamp(String timeStamp) throws ParseException {
		String stringArr = timeStamp.replace("+0000","");
		stringArr = stringArr.replace("  "," ");
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
		Date d1 = df.parse(stringArr);
		Date d2 = new Date();
		long diff = d2.getTime() - d1.getTime();
		long diffInMilliseconds = Math.abs(d1.getTime() - d2.getTime());
		long diffSeconds = diffInMilliseconds /1000;
		if(diffSeconds < 60){
			return Long.toString(diffSeconds) + "s";
		}
		else if((diffSeconds/60) < 60){
			return Long.toString(diffSeconds/60) + "m";
		}
		else if((diffSeconds/(60*60))< 24){
			return Long.toString(diffSeconds/(60*60)) + "h";
		}
		else{
			return Long.toString(diffSeconds/(24 * 60 * 60)) + "d";
		}
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
