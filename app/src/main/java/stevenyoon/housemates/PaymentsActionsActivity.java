package stevenyoon.housemates;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class PaymentsActionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CONSUMER_KEY = "zxZolcntA2h9IO76sEUg818wmU92QQcduwpPrR4d";
    private static final String CONSUMER_SECRET = "YXJ8XpBVimRlDq90dLD5w7RpKXKZLqPxzGJl6Mr8";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://secure.splitwise.com/api/v3.0/get_access_token";
    private static final String AUTHORIZATION_URL = "https://secure.splitwise.com/authorize";

    private CommonsHttpOAuthConsumer consumer;
    private CommonsHttpOAuthProvider provider;
    private HashMap<String, Group> groups;
    private List<User> members;
    private ArrayList<GroupMember> membersList;
    private ListView listItems;
    private MyAdapter adapt;
    private String createExpensesUrl;

    private String group;
    private Group housemates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_actions);

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

        group = getIntent().getStringExtra("group");
        //getSupportActionBar().hide();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        members = new ArrayList<User>();
        adapt = new MyAdapter(this, R.layout.list_inner_view, members);
        listItems = (ListView) findViewById(R.id.member_names);
        listItems.setAdapter(adapt); //itemsAdapter
        setupListViewListener();
        housemates = groups.get("housemates");
        membersList = housemates.getMembers();

        for(int i=1; i<membersList.size(); i++){
            GroupMember g = membersList.get(i);
            User user = new User(g.getName(), 0, g.getId());
            adapt.add(user);
            adapt.notifyDataSetChanged();
        }

        //createExpense(housemates);
    }


    public void createExpense(View v){
        ArrayList<String> userIds = new ArrayList<String>();
        userIds.add(Integer.toString(membersList.get(0).getId()));
        for(User user : adapt.taskList){
            if (user.getStatus() == 1) {
                userIds.add(Integer.toString(user.getId()));
            }
        }

        EditText c = (EditText) findViewById(R.id.costs);
        EditText d = (EditText) findViewById(R.id.description);
        int groupId = housemates.getId();
        int cost100 = Integer.parseInt(c.getText().toString()) * 100;
        int cost = cost100 / 100;
        //int remainder = cost100 % userIds.size();

        String description = d.getText().toString();
        double owedAmount = cost / (double)userIds.size();
        owedAmount = Math.round(owedAmount * 100.0) / 100.0;
        System.out.println("Owed amount: " + owedAmount);
        System.out.println("num of users: " + userIds.size());

        createExpensesUrl = getExpensesURLBuilder(Double.toString(cost), description, groupId,
                userIds, owedAmount, cost);

        Thread t = new CreateExpenseThread();
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Charge Completed!", Toast.LENGTH_LONG).show();
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

        //};
        //oauth.start();
    }

    }

    public String getExpensesURLBuilder(String cost, String description, int groupId, ArrayList<String> members,
                                      double owedAmount, double paidAmount) {
        double remainder = 0;
        if(owedAmount * members.size() != paidAmount){
            remainder = paidAmount - (owedAmount * members.size());
        }
        String url = "payment=0&cost=" + cost + "&description=" + description + "&group_id=" +
                Integer.toString(groupId) + "&users__0__user_id=" + members.get(0)
                + "&users__0__paid_share=" + Double.toString(paidAmount) + "&users__0__owed_share="
                + Double.toString(owedAmount + remainder) + "&";
        //memberIds.remove(0);
        for(int i=1; i<members.size(); i++){
            url = url + "users__" + Integer.toString(i) + "__user_id=" + members.get(i) +
                    "&users__" + Integer.toString(i) + "__owed_share=" + Double.toString(owedAmount);
            if(i != members.size() - 1) {
                url = url + "&";
            }
        }

        return url;

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


    private class CreateExpenseThread extends Thread {
        //Thread oauth = new Thread() {
            @Override
            public void run() {
                Group housemates = groups.get("housemates");
                int housematesId = housemates.getId();
                ArrayList<GroupMember> members = housemates.getMembers();
                //String url = getExpensesURLBuilder()

                HttpPost request = new HttpPost("https://secure.splitwise.com/api/v3.0/create_expense");
                // sign the request
                try {
                    ArrayList<NameValuePair> postParameters;

                   /* StringEntity body = new StringEntity("payment=0&cost=20&description=milk&group_id=1668899&" +
                            "users__0__user_id=3651518&users__0__paid_share=20&" +
                            "users__1__user_id=3557621&users__1__owed_share=20"); */

                    StringEntity body = new StringEntity(createExpensesUrl);
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

        //};
        //oauth.start();
    }
    private class MyAdapter extends ArrayAdapter<User> {
        Context context;
        List<User> taskList = new ArrayList<User>();
        int layoutResourceId;

        public MyAdapter(Context context, int layoutResourceId,
                         List<User> objects) {
            super(context, layoutResourceId, objects);
            this.layoutResourceId = layoutResourceId;
            this.taskList = objects;
            this.context = context;
        }

        /**
         * This method will DEFINe what the view inside the list view will
         * finally look like Here we are going to code that the checkbox state
         * is the status of task and check box text is the task name
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckBox chk = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_inner_view,
                        parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(chk);
                chk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        User checkUser = (User) cb.getTag();
                        checkUser.changeStatus();

                    }
                });
            } else {
                chk = (CheckBox) convertView.getTag();
            }
            User current = taskList.get(position);
            chk.setText(current.getName());
            chk.setChecked(current.getStatus() == 1 ? true : false);
            chk.setTag(current);
            Log.d("listener", String.valueOf(current.getId()));
            return convertView;
        }
    }

    private void setupListViewListener() {
        listItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        User t = members.get(pos);
                        t.getId();
                        //Firebase ref = new Firebase("https://dazzling-torch-3636.firebaseio.com");
                        //ref=ref.child("groups").child(group).child("tasks").child(t.getId());
                        //ref.removeValue();
                        members.remove(pos);
                        adapt.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.tasks_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home){
            Intent i = new Intent(PaymentsActionsActivity.this, MainActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if(id == R.id.nav_calendar) {
            Intent i = new Intent(PaymentsActionsActivity.this, CalendarActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if(id == R.id.nav_tasks) {
            Intent i = new Intent(PaymentsActionsActivity.this, TasksActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }

        else if (id == R.id.nav_payment) {
            Intent i = new Intent(PaymentsActionsActivity.this, SplitwiseActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        /*else if (id == R.id.nav_settings) {
            Intent i = new Intent(PaymentsActionsActivity.this, SettingsActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
