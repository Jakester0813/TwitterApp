package com.jakester.twitterapp.util;

/**
 * Created by Jake on 9/25/2017.
 */

public class TwitterContstants {

    public final String TWITTER_API_KEY="kKUASTPcutl01JsIsI5srBkPD";
    public final String TWITTER_API_KEY_SECRET="hyxeKL3GJBLHacwIaDDMXzZWO06Am2ua6aj6v4aMHHnSF9qkxD";

    public static final String BASE_TWITTER_URL="https://api.twitter.com/1.1/";

    public static final String REST_CONSUMER_KEY = "kKUASTPcutl01JsIsI5srBkPD";       // Change this
    public static final String REST_CONSUMER_SECRET = "hyxeKL3GJBLHacwIaDDMXzZWO06Am2ua6aj6v4aMHHnSF9qkxD"; // Change this

    // Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
    public static final String FALLBACK_URL = "http://codepath.github.io/android-rest-client-template/success.html";

    // See https://developer.chrome.com/multidevice/android/intents
    public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public static final String COMPOSE_TWEET = "Compose Tweet";
    public static final String FRAGMENT_TWEET = "fragment_new_tweet";

    public static final String NETWORK_EXEC = "/system/bin/ping -c 1 8.8.8.8";

    public static final String NO_INTERNET_TITLE = "No Internet";
    public static final String NO_INTERNET_TEXT = "It seems that you are not connected to the internet. Make sure that you are connected before trying again";

    public static final String DEFAULT_BANNER_URL = "http://abs.twimg.com/images/themes/theme1/bg.png";

    public static final String NO_TWEETS_TITLE = "No tweets";
    public static final String NO_TWEETS_TEXT = "No found. Please check your input and try again.";
}
