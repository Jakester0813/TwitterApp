package com.jakester.twitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.jakester.twitterapp.R;
import com.jakester.twitterapp.application.TwitterApplication;
import com.jakester.twitterapp.models.User;
import com.jakester.twitterapp.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	TwitterClient mClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mClient = TwitterApplication.getRestClient();
		if(mClient.isAuthenticated()){
			onLoginSuccess();
		}
		setContentView(R.layout.activity_login);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Intent i = new Intent(LoginActivity.this, HomeTimelineActivity.class);
		startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {

		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {

		mClient.setRequestIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mClient.connect();
	}

	public void forgotPassword(View view) {
		Toast.makeText(this, "Oops, sorry, forgot that I can't do that!", Toast.LENGTH_LONG).show();
	}

}
