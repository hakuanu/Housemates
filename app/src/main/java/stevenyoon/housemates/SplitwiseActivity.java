package stevenyoon.housemates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


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
import com.google.gdata.util.common.base.PercentEscaper;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.commonshttp.HttpRequestAdapter;
import oauth.signpost.commonshttp.HttpResponseAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import oauth.signpost.signature.SigningStrategy;

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
    private static final String OAUTH_NONCE = "oVnT0q6VBYeYD2wAXuZww1N8Hk7qSJi8uU1szIrOAvA";
    private static final String oauth_signature = "V9w9%2Bwu0xTQlfRJvsBOskzATlNg%3D";
    private static final String oauth_signature_method = "HMAC-SHA1";
    private static final String oauth_timestamp= "1324583427";
    private static final String oauth_version="1.0";

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private ProgressDialog pd;
    private CommonsHttpOAuthConsumer consumer;
    private CommonsHttpOAuthProvider provider;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitwise);
        //get the webView from the layout
        webView = (WebView) findViewById(R.id.splitwise_activity_web_view);

        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);
        //Show a progress dialog to the user
        pd = ProgressDialog.show(this, "", "loading",true);

        // create a consumer object and configure it with the access
        consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
        provider = new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZE_WEBSITE_URL);
        //RequestToken requestToken = new RequestToken(consumer, provider);
        //authUrl = getRequestToken(consumer, provider);

        final ExecutorService service;
        final Future<String> task;

        service = Executors.newFixedThreadPool(1);
        task    = service.submit(new RequestToken(consumer, provider));

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
                    //We make the request in a AsyncTask
                System.out.println("Now visit:\n" + authorizationUrl + "\n... and grant this app authorization");
                webView.loadUrl(authorizationUrl);
                Uri uri = Uri.parse(authorizationUrl);
                if (uri != null && uri.toString().contains("oauth_verifier")) {
                    // oAuth verifier
                    String verifier = uri.getQueryParameter("oauth_verifier");
                    System.out.println("Verifier is " + verifier);
                    new PostRequestAsyncTask().execute(verifier);
                }
                return true;

                /*if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Run PostSyncTask", "Running Task!");
                    try {
                        authorizationUrl = task.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    new PostRequestAsyncTask().execute(consumer.getToken());
                }
                else {
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true; */
            }
        });

        try {
            String authUrl = task.get();
            //Load the authorization URL into the webView
            webView.loadUrl(authUrl);
            Log.i("Authorize", "Loading Auth Url : " + authUrl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(SplitwiseActivity.this, "", "loading",true);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            getAccessToken(consumer, provider, urls[0]);
            //String authUrl = ACCESS_TOKEN_URL;
            /*authUrl = OAuth.addQueryParameters(authUrl, OAuth.OAUTH_CONSUMER_KEY, CONSUMER_KEY,
                    OAuth.OAUTH_NONCE, OAUTH_NONCE, OAuth.OAUTH_SIGNATURE, oauth_signature,
                    OAuth.OAUTH_SIGNATURE_METHOD, oauth_signature_method, OAuth.OAUTH_TIMESTAMP,
                    oauth_timestamp, OAuth.OAUTH_TOKEN, consumer.getToken(),OAuth.OAUTH_VERIFIER, consumer.getTokenSecret(),
                    OAuth.OAUTH_VERSION, oauth_version); */

           /* HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(authUrl);

            HttpConnectionParams.setSoTimeout(request.getParams(), 10000);
            HttpConnectionParams.setConnectionTimeout(request.getParams(), 10000); // Timeout

            request.setHeader("oauth_consumer_key", CONSUMER_KEY);
            request.setHeader("oauth_nonce", "oVnT0q6VBYeYD2wAXuZww1N8Hk7qSJi8uU1szIrOAvA");
            request.setHeader("oauth_signature", "V9w9%2Bwu0xTQlfRJvsBOskzATlNg%3D");
            request.setHeader("oauth_signature_method", "HMAC-SHA1");
            request.setHeader("oauth_timestamp", "1324583427");
            request.setHeader("oauth_token", consumer.getToken());
            request.setHeader("oauth_verifier", consumer.getTokenSecret());
            request.setHeader("oauth_version", "1.0");
            Log.i("Authorize", "Loading Auth Url : " + request.getURI().toASCIIString());
            HttpRequest send = new HttpRequestAdapter(request);
            try {
                //oauth.signpost.http.HttpResponse response = new HttpResponseAdapter(httpClient.execute((HttpUriRequest)send.unwrap()));
                HttpResponse response = httpClient.execute(request);

                String responseString = new BasicResponseHandler().handleResponse(response);
                System.out.println(responseString);

               // HttpParameters responseParams = OAuth.decodeForm(response.getContent());
                String token = responseParams.getFirst("oauth_token");
                String secret = responseParams.getFirst("oauth_token_secret");
                System.out.println("Access token: " + token);
                System.out.println("Token secret: " + secret);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } */

            return true;


        }

        @Override
        protected void onPostExecute(Boolean status){
            if(status){
                //If everything went Ok, change to another activity.
                Log.v("Debugging", "worked!");
                System.out.println("Access token: " + consumer.getToken());
                System.out.println("Token secret: " + consumer.getTokenSecret());
                Intent startProfileActivity = new Intent(SplitwiseActivity.this, PaymentsActivity.class);
                SplitwiseActivity.this.startActivity(startProfileActivity);
            }
            else{
                Log.v("Debugging", "request was null");
                System.out.println("Access token: " + consumer.getToken());
                System.out.println("Token secret: " + consumer.getTokenSecret());
                Intent startProfileActivity = new Intent(SplitwiseActivity.this, PaymentsActivity.class);
                SplitwiseActivity.this.startActivity(startProfileActivity);
            }
        }

    }


    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private class RequestToken implements Callable<String> {
        private CommonsHttpOAuthConsumer consumer;
        private CommonsHttpOAuthProvider provider;

        public RequestToken(CommonsHttpOAuthConsumer consumer, CommonsHttpOAuthProvider provider){
            this.consumer = consumer;
            this.provider = provider;
        }

        public String call() {
            String authUrl = "";
                try {
                    authUrl = provider.retrieveRequestToken(consumer, AUTHORIZATION_URL);
                    System.out.println("Request token: " + consumer.getToken());
                    System.out.println("Token secret: " + consumer.getTokenSecret());
                    /*authUrl = OAuth.addQueryParameters(authUrl, OAuth.OAUTH_CONSUMER_KEY, CONSUMER_KEY,
                            OAuth.OAUTH_NONCE, OAUTH_NONCE, OAuth.OAUTH_SIGNATURE, oauth_signature,
                            OAuth.OAUTH_SIGNATURE_METHOD, oauth_signature_method,OAuth.OAUTH_VERIFIER, consumer.getTokenSecret(),
                            OAuth.OAUTH_TIMESTAMP, oauth_timestamp, OAuth.OAUTH_VERSION, oauth_version); */
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }

            return authUrl;
        }
    }

    private void getAccessToken(final CommonsHttpOAuthConsumer consumer, final CommonsHttpOAuthProvider provider, final String verifier) {

        Thread oauth = new Thread() {
            @Override
            public void run() {

                try {
                    provider.retrieveAccessToken(consumer, verifier);
                    Log.i("Got Token!", "Access Token " + consumer.getToken());
                    Log.i("Got Token!", "Access Secret " + consumer.getTokenSecret());

                    HttpPost request = new HttpPost("https://secure.splitwise.com/api/v3.0/test");
                    consumer.sign(request);

                    System.out.println("Sending  request to Spltiwise...");

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = null;
                    try {
                        response = httpClient.execute(request);

                    String msg = EntityUtils.toString(response.getEntity(), "UTF-8");
                    System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
                            + response.getStatusLine().getReasonPhrase());
                    System.out.println(msg.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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


}
