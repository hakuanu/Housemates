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
import com.google.api.client.http.HttpRequestFactory;
//import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
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

    /****FILL THIS WITH YOUR INFORMATION*********/
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
    private static final String RESPONSE_TYPE_VALUE ="code";
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

    /*---------------------------------------*/
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private WebView webView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitwise);

        //get the webView from the layout
        webView = (WebView)findViewById(R.id.splitwise_activity_web_view);

        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);


        //Show a progress dialog to the user
        pd = ProgressDialog.show(this, "", "loading",true);
        webView.getSettings().setJavaScriptEnabled(true);


        //Set a custom web view client
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //This method will be executed each time a page finished loading.
                //The only we do is dismiss the progressDialog, in case we are showing any.
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {

                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                    //If not, that means the request may be a result of CSRF and must be rejected.
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(STATE)) {
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    //Generate URL for requesting Access Token
                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);

                    //We make the request in a AsyncTask
                    new PostRequestAsyncTask().execute(accessTokenUrl);

                } else {
                    //Default behaviour
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        //Get the authorization Url
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        //Load the authorization URL into the webView
        webView.loadUrl(authUrl);

/*
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider
        CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET);

        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
        CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);
        // fetches a request token from the service provider and builds
        // a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
        // which your app must now send the user
        try {

            //consumer.setTokenWithSecret(ACCESS_TOKEN, TOKEN_SECRET);

            //Show authorization in view
            webView = (WebView)findViewById(R.id.splitwise_activity_web_view);
            //Request focus for the webview
            webView.requestFocus(View.FOCUS_DOWN);
            //Show a progress dialog to the user
            pd = ProgressDialog.show(this, "", "loading",true);
            webView.getSettings().setJavaScriptEnabled(true);

            String authURL = provider.retrieveRequestToken(consumer, "http://www.example.com");
            webView.loadUrl(authURL);

            Log.v("Request Token", consumer.getToken());
            Log.v("Token Secret", consumer.getTokenSecret());

            provider.retrieveAccessToken(consumer, "");

            Log.v("Access Token", consumer.getToken());
            Log.v("Token Secret", consumer.getTokenSecret());


            //URL requestURL = new URL("https://secure.splitwise.com/api/v3.0/get_groups");

            // create an HTTP request to a protected resource
            HttpGet request = new HttpGet("https://secure.splitwise.com/api/v3.0/get_groups");

            consumer.sign(request);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);

            Log.v("Response Code", Integer.toString(response.getStatusLine().getStatusCode()));
            Log.v("Response Message", response.getStatusLine().getReasonPhrase());


        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }  catch (MalformedURLException e) {
        e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/



    }

    /**
     * Method that generates the url for get the access token from the Service
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken){
        return ACCESS_TOKEN_URL
                +QUESTION_MARK
                +GRANT_TYPE_PARAM+EQUALS+GRANT_TYPE
                +AMPERSAND
                +RESPONSE_TYPE_VALUE+EQUALS+authorizationToken
                +AMPERSAND
                +CLIENT_ID_PARAM+EQUALS+API_KEY
                +AMPERSAND
                +REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI
                +AMPERSAND
                +SECRET_KEY_PARAM+EQUALS+SECRET_KEY;
    }
    /**
     * Method that generates the url for get the authorization token from the Service
     * @return Url
     */

    private static String getAuthorizationUrl(){
        return AUTHORIZATION_URL
                +QUESTION_MARK+RESPONSE_TYPE_PARAM+EQUALS+RESPONSE_TYPE_VALUE
                +AMPERSAND+CLIENT_ID_PARAM+EQUALS+API_KEY
                +AMPERSAND+STATE_PARAM+EQUALS+STATE
                +AMPERSAND+REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(SplitwiseActivity.this, "", "loading",true);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if(urls.length>0){
                String url = "https://secure.splitwise.com/api/v3.0/get_groups";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(url);
                try{
                    //FIX THIS (check what the response is, what to parse, etc)
                    HttpResponse response = httpClient.execute(httpost);
                    String responseBody = EntityUtils.toString(response.getEntity());
                    Log.v("Debugging", responseBody);
                    if(response!=null){
                        //If status is OK 200
                        if(response.getStatusLine().getStatusCode()==200){
                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object
                            JSONObject resultJson = new JSONObject(result);
                            //Extract data from JSON Response
                            int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

                            String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                            Log.e("Tokenm", ""+accessToken);
                            if(expiresIn>0 && accessToken!=null){
                                Log.i("Authorize", "This is the access Token: "+accessToken+". It will expires in "+expiresIn+" secs");

                                //Calculate date of expiration
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.SECOND, expiresIn);
                                long expireDate = calendar.getTimeInMillis();

                                ////Store both expires in and access token in shared preferences
                                SharedPreferences preferences = SplitwiseActivity.this.getSharedPreferences("user_info", 0);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putLong("expires", expireDate);
                                editor.putString("accessToken", accessToken);
                                editor.commit();

                                return true;
                            }
                        }
                    }
                }catch(IOException e){
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                }
                catch (ParseException e) {
                    Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status){
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            if(status){
                //If everything went Ok, change to another activity.
                Log.v("Debugging", "worked!");
                Intent startProfileActivity = new Intent(SplitwiseActivity.this, MainActivity.class);
                SplitwiseActivity.this.startActivity(startProfileActivity);
            }
            else{
                Log.v("Debugging", "request was null");
                Intent startProfileActivity = new Intent(SplitwiseActivity.this, MainActivity.class);
                SplitwiseActivity.this.startActivity(startProfileActivity);
            }
        }

    }
}
