package com.jakester.twitterapp.network;

import android.content.Context;


import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.util.TwitterContstants;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jakester.twitterapp.util.TwitterContstants;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();


	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				TwitterContstants.BASE_TWITTER_URL,
				TwitterContstants.REST_CONSUMER_KEY,
				TwitterContstants.REST_CONSUMER_SECRET,
				String.format(TwitterContstants.REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), TwitterContstants.FALLBACK_URL));
	}

	private static Retrofit retrofit = null;

	public static Retrofit getRetrofitClient() {
		retrofit = new Retrofit.Builder()
				.baseUrl(TwitterContstants.BASE_TWITTER_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.build();



		return retrofit;
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count",25);
		params.put("page", String.valueOf(page));
		client.get(apiUrl, params, handler);
	}

    public void getSearchResult(String q, String max_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("q",q);
		if(!max_id.equals(""))
        	params.put("max_id", max_id);
        client.get(apiUrl, params, handler);
    }

	public void getMentionsTimeline(String maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count",40);
		if(!maxId.equals(""))
			params.put("max_id",maxId);
		client.get(apiUrl, params, handler);
	}

	public void getDirectMessages(String maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("direct_messages.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count",40);
		if(!maxId.equals(""))
			params.put("max_id",maxId);
		client.get(apiUrl, params, handler);
	}


	// TwitterClient.java
	public void postTweet(String body, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", body);


		getClient().post(apiUrl, params, handler);
	}

	public void favoriteTweet(boolean favorited, String id, AsyncHttpResponseHandler handler) {

		String apiUrl = favorited ? getApiUrl("favorites/destroy.json") :
				getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, handler);
	}

	public void reTweet(boolean retweeted, String id, AsyncHttpResponseHandler handler) {

		String apiUrl = retweeted ? getApiUrl("statuses/unretweet/"+id+".json") :
				getApiUrl("statuses/retweet/"+id+".json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, handler);
	}

	public void getUser(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(String id, String maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("user_id", id);
		if(!maxId.equals(""))
			params.put("max_id",maxId);
		client.get(apiUrl, params, handler);
	}

	public void getFollowers(String id, int page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = new RequestParams();
		params.put("user_id", id);
		params.put("count",50);
		params.put("page", page);
		getClient().get(apiUrl, params, handler);
	}

	public void getFollowing(String id, int page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = new RequestParams();
		params.put("user_id", id);
		params.put("count",50);
		params.put("page", page);
		getClient().get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
