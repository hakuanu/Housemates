package stevenyoon.housemates;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
//import com.google.api.client.http.HttpRequestFactory;
//import com.google.api.client.http.HttpResponse;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import org.apache.http.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class SplitwiseActivity extends AppCompatActivity {

    /*CONSTANT FOR THE AUTHORIZATION PROCESS*/

    /****
     * FILL THIS WITH YOUR INFORMATION
     *********/
//This is the public api key of our application
    private static final String API_KEY = "zxZolcntA2h9IO76sEUg818wmU92QQcduwpPrR4d";
    //This is the private api key of our application
    private static final String SECRET_KEY = "YXJ8XpBVimRlDq90dLD5w7RpKXKZLqPxzGJl6Mr8";
    //This is any string we want to use. This will be used for avoid CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
    private static final String STATE = "AAAAA";
    //This is the url that Auth process will redirect to. We can put whatever we want that starts with http:// or https:// .
//We use a made up url that we will intercept when redirecting. Avoid Uppercases.
    private static final String REDIRECT_URI = "";
    /*********************************************/

//These are constants used for build the urls
    private static final String AUTHORIZATION_URL = "https://secure.splitwise.com/authorize";
    private static final String ACCESS_TOKEN_URL = "https://secure.splitwise.com/api/v3.0/get_access_token";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    private static final String CONSUMER_KEY = "zxZolcntA2h9IO76sEUg818wmU92QQcduwpPrR4d";
    private static final String CONSUMER_SECRET = "YXJ8XpBVimRlDq90dLD5w7RpKXKZLqPxzGJl6Mr8";
    private String ACCESS_TOKEN = "";
    private String TOKEN_SECRET = "";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_access_token";
    private static final String AUTHORIZE_WEBSITE_URL = "https://secure.splitwise.com/authorize";
    private static final String CALLBACK_URL = null;

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private ProgressDialog pd;
    //final String authUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitwise);
        //get the webView from the layout
        WebView webView = (WebView) findViewById(R.id.splitwise_activity_web_view);

        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);


        //Show a progress dialog to the user
        //pd = ProgressDialog.show(this, "", "loading", true);
        //webView.getSettings().setJavaScriptEnabled(true);

        // create a consumer object and configure it with the access
        final CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
        CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);
        String authUrl = getRequestToken(consumer, provider);
        //Load the authorization URL into the webView
        Log.i("Authorize", "Loading Auth Url : " + authUrl);
        webView.loadUrl(authUrl);
        getAccessToken(consumer, provider);
    }

    private String getRequestToken(final CommonsHttpOAuthConsumer consumer, final CommonsHttpOAuthProvider provider) {
        final String authUrl;
        Thread oauth = new Thread() {
            @Override
            public void run() {


                // fetches a request token from the service provider and builds
                // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
                // which your app must now send the user
                try {

                    //FIX ACCESSING URL OUT OF THREAD
                    authUrl = provider.retrieveRequestToken(consumer, "");

                    System.out.println("Request token: " + consumer.getToken());
                    System.out.println("Token secret: " + consumer.getTokenSecret());

                    //Get the authorization Url
                    //String authUrl = getAuthorizationUrl();
                    //Log.i("Authorize", "Loading Auth Url : " + authUrl);


                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }


            }

        };
        oauth.start();
        return authUrl;
    }

    private void getAccessToken(final CommonsHttpOAuthConsumer consumer, final CommonsHttpOAuthProvider provider) {

        Thread oauth = new Thread() {
            @Override
            public void run() {
                // fetches a request token from the service provider and builds
                // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
                // which your app must now send the user
                try {
                    provider.retrieveAccessToken(consumer, null);
                    Log.i("Got Token!", "Access Token " + consumer.getToken());
                    Log.i("Got Token!", "Access Secret " + consumer.getTokenSecret());

                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }
            }

        };
        oauth.start();
    }


    private static String getAuthorizationUrl() {
        return AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI;
    }

}
