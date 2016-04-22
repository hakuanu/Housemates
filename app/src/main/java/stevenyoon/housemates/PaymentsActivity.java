package stevenyoon.housemates;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class PaymentsActivity extends AppCompatActivity {

    WebView web;

    private static final String CONSUMER_KEY = "zxZolcntA2h9IO76sEUg818wmU92QQcduwpPrR4d";
    private static final String CONSUMER_SECRET = "YXJ8XpBVimRlDq90dLD5w7RpKXKZLqPxzGJl6Mr8";
    private String ACCESS_TOKEN = "";
    private String TOKEN_SECRET = "";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_access_token";
    private static final String AUTHORIZATION_URL = "https://secure.splitwise.com/authorize";

    private CommonsHttpOAuthConsumer consumer;
    private CommonsHttpOAuthProvider provider;
    private String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        Button auth = (Button) findViewById(R.id.auth_button);
        group = getIntent().getStringExtra("group");

        consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
        provider = new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZATION_URL);

        auth.setOnClickListener(
                new View.OnClickListener() {
                    Dialog auth_dialog;

                    @Override
                    public void onClick(View arg0) {
                        auth_dialog = new Dialog(PaymentsActivity.this);
                        auth_dialog.setContentView(R.layout.activity_splitwise);
                        web = (WebView) auth_dialog.findViewById(R.id.splitwise_activity_web_view);
                        web.getSettings().setJavaScriptEnabled(true);
                        final ExecutorService service;
                        final Future<String> task;

                        service = Executors.newFixedThreadPool(1);
                        task = service.submit(new RequestToken(consumer, provider));
                        try {
                            String authUrl = task.get();
                            //Load the authorization URL into the webView
                            web.loadUrl(authUrl);
                            Log.i("Authorize", "Loading Auth Url : " + authUrl);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        web.setWebViewClient(new WebViewClient() {

                            boolean authComplete = false;
                            Intent resultIntent = new Intent();

                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                super.onPageStarted(view, url, favicon);

                            }

                            String authCode;

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                Uri uri = Uri.parse(url);

                                if (uri != null && uri.toString().contains("oauth_verifier")) {
                                    // oAuth verifier
                                    String verifier = uri.getQueryParameter("oauth_verifier");
                                    System.out.println("Verifier is " + verifier);
                                    authComplete = true;
                                    resultIntent.putExtra("code", authCode);
                                    resultIntent.putExtra("group", group);
                                    PaymentsActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                                    setResult(Activity.RESULT_CANCELED, resultIntent);
                                    auth_dialog.dismiss();

                                    new AuthorizeToken().execute(verifier);

                                    //Toast.makeText(getApplicationContext(),"Authorization Code is: " +authCode, Toast.LENGTH_SHORT).show();
                                } else if(url.contains("error=access_denied")){
                                    Log.i("", "ACCESS_DENIED_HERE");
                                    resultIntent.putExtra("code", authCode);
                                    resultIntent.putExtra("group", group);
                                    authComplete = true;
                                    setResult(Activity.RESULT_CANCELED, resultIntent);
                                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

                                    auth_dialog.dismiss();
                                }
                            }
                        });
                        auth_dialog.show();
                        auth_dialog.setTitle("Authorize Splitwise");
                        auth_dialog.setCancelable(true);
                    }
                });
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

    private class AuthorizeToken extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog pd;
        String Code;

        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(PaymentsActivity.this);
            pd.setMessage("Contacting Splitwise ...");
            pd.setIndeterminate(false);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            getAccessToken(consumer, provider, urls[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean status){
            pd.dismiss();
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
                        Intent i = new Intent(PaymentsActivity.this, PaymentsActionsActivity.class);
                        i.putExtra("access_token", consumer.getToken());
                        i.putExtra("access_secret", consumer.getTokenSecret());
                        i.putExtra("group", group);
                        System.out.println("Passing Token " + consumer.getToken());
                        System.out.println("Passing Secret " + consumer.getTokenSecret());
                        startActivity(i);
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


