package stevenyoon.housemates;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.api.client.json.Json;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class PaymentsActionsActivity extends AppCompatActivity {

    private static final String CONSUMER_KEY = "zxZolcntA2h9IO76sEUg818wmU92QQcduwpPrR4d";
    private static final String CONSUMER_SECRET = "YXJ8XpBVimRlDq90dLD5w7RpKXKZLqPxzGJl6Mr8";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_access_token";
    private static final String AUTHORIZATION_URL = "https://secure.splitwise.com/authorize";

    private CommonsHttpOAuthConsumer consumer;
    private CommonsHttpOAuthProvider provider;
    private HashMap<String, Group> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_payments_actions);

        Bundle intentExtras = getIntent().getExtras();

        String token = intentExtras.getString("access_token");
        String secret = intentExtras.getString("access_secret");

        Log.i("Got Token new", "Access Token " + token);
        Log.i("Got Secret new", "Access Secret " + secret);

        consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

        consumer.setTokenWithSecret(token, secret);
        // create a new service provider object and configure it with
        // the URLs which provide request tokens, access tokens, and
        // the URL to which users are sent in order to grant permission
        // to your application to access protected resources
        provider = new CommonsHttpOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
                AUTHORIZATION_URL);
        //getCurrentUser();

        //get groups
        Thread t = new GetGroupsThread();
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            Log.v("Thread Error:", "Thread not finished");
            e.printStackTrace();
        }

      

       // createExpense();
    }


    public void getCurrentUser(){
        Thread oauth = new Thread() {
            @Override
            public void run() {
                HttpPost request = new HttpPost("https://secure.splitwise.com/api/v3.0/get_current_user");
                // sign the request
                try {
                    consumer.sign(request);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(request);
                    String msg = EntityUtils.toString(response.getEntity(), "UTF-8");
                    System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
                            + response.getStatusLine().getReasonPhrase());
                    System.out.println("Current User info: " + msg.toString());
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        oauth.start();
    }

    private class GetGroupsThread extends Thread {
        //Thread oauth = new Thread() {
            @Override
            public void run() {
                HttpGet request = new HttpGet("https://secure.splitwise.com/api/v3.0/get_groups");
                // sign the request
                try {
                    consumer.sign(request);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(request);
                    String json_string = EntityUtils.toString(response.getEntity(), "UTF-8");
                    JSONObject msg = new JSONObject(json_string);
                    JSONArray groupsList = msg.getJSONArray("groups");

                   groups = new HashMap<String, Group>();

                    for(int i=0; i<groupsList.length(); i++){
                        int groupId = Integer.parseInt(groupsList.getJSONObject(i).getString("id"));
                        String groupName = groupsList.getJSONObject(i).getString("name");
                        Group g = new Group(groupId, groupName );

                        JSONArray membersList = groupsList.getJSONObject(i).getJSONArray("members");
                        //System.out.println("Group info: " + groupId + " " + groupName);
                        //parseMembers in groups
                        for(int j =0; j< membersList.length(); j++){
                            int memberId = Integer.parseInt(membersList.getJSONObject(j).getString("id"));
                            String memberName = membersList.getJSONObject(j).getString("first_name") + " "
                                    + membersList.getJSONObject(j).getString("last_name");
                            JSONArray memberBalanceArray = membersList.getJSONObject(j).getJSONArray("balance");
                            String memberBalance = "";
                            if(memberBalanceArray.length() != 0){
                                memberBalance = memberBalanceArray.getJSONObject(0).getString("amount");
                            }
                            GroupMember member;
                            if(memberBalance.isEmpty()) {
                                 member = new GroupMember(memberId, memberName, "0");
                            }else{
                                 member = new GroupMember(memberId, memberName, memberBalance);

                            }
                            g.addMember(member);
                            //System.out.println("Member info: " + memberId + " " + memberName + " " + memberBalance);
                        }
                        groups.put(groupName, g);
                    }
                    //System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
                            //+ response.getStatusLine().getReasonPhrase());
                    //System.out.println("Group info: " + msg.toString());
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        //};
        //oauth.start();
    }

    public void createExpense(){
        Thread oauth = new Thread() {
            @Override
            public void run() {
                HttpPost request = new HttpPost("https://secure.splitwise.com/api/v3.0/create_expense");
                // sign the request
                try {
                    ArrayList<NameValuePair> postParameters;

                    StringEntity body = new StringEntity("payment=1&cost=20&description=milk&group_id=1668899&" +
                            "users__0__user_id=3651518&users__0__paid_share=20&" +
                            "users__1__user_id=3557621&users__1__owed_share=20");
                    body.setContentType("application/x-www-form-urlencoded");
                    request.setEntity(body);
                    consumer.sign(request);

                    System.out.println("Request URL: " + EntityUtils.toString(request.getEntity()));

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(request);
                    String json_string = EntityUtils.toString(response.getEntity(), "UTF-8");
                    JSONObject msg = new JSONObject(json_string);


                    System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
                            + response.getStatusLine().getReasonPhrase());
                    System.out.println("Expense info: " + msg.toString());
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
        oauth.start();
    }
}
