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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public String group;
    private List<Task> smallList;
    private List<Event> smallEventList;
    private MyAdapter adapt;
    private EventAdapter adaptEvents;
    private ListView listItems;
    private ListView eventListItems;
    private  ValueEventListener vel;
    private int tasks;
    private int events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        group = getIntent().getStringExtra("group");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        smallList = new ArrayList<Task>();
        adapt = new MyAdapter(this, R.layout.list_inner_view, smallList);
        listItems = (ListView) findViewById(R.id.listedItems);
        listItems.setAdapter(adapt); //itemsAdapter
        smallEventList = new ArrayList<Event>();
        adaptEvents = new EventAdapter(this, R.layout.list_inner_view, smallEventList);
        eventListItems = (ListView) findViewById(R.id.listedEvents);
        eventListItems.setAdapter(adaptEvents); //itemsAdapter
        tasks = 0;
        events =0;
        loadTasksFromDB();
    }

    private void loadTasksFromDB(){
        Firebase ref = new Firebase("https://dazzling-torch-3636.firebaseio.com");
        ref = ref.child("groups").child(group);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.child("tasks").getChildren()) {
                    String desc = (String) child.child("description").getValue();
                    String status = (String) child.child("status").getValue();
                    Task task = new Task(desc, Integer.parseInt(status),child.getKey());
                    if (tasks < 5) {
                        adapt.add(task);
                        adapt.notifyDataSetChanged();
                        tasks++;
                    }
                }
                for (DataSnapshot child : snapshot.child("events").getChildren()) {
                    String eventDate = (String)child.child("event_date").getValue();
                    String eventTimeS = (String)child.child("event_time_start").getValue();
                    String eventTimeE = (String)child.child("event_time_end").getValue();
                    String eventClub = (String)child.child("event_club").getValue();
                    String eventName = (String)child.child("event_name").getValue();
                    String eventDetails = (String)child.child("event_details").getValue();
                    Event event = new Event(eventDate, eventTimeS, eventTimeE, eventName, eventClub, eventDetails, child.getKey());
                    adaptEvents.add(event);
                    adaptEvents.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_calendar) {
            Intent i = new Intent(MainActivity.this, CalendarActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if (id == R.id.nav_tasks) {
            Intent i = new Intent(MainActivity.this, TasksActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if (id == R.id.nav_payment) {
            Intent i = new Intent(MainActivity.this, PaymentsActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
       /* else if (id == R.id.nav_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class MyAdapter extends ArrayAdapter<Task> {
        Context context;
        List<Task> taskList = new ArrayList<Task>();
        int layoutResourceId;
        public MyAdapter(Context context, int layoutResourceId,
                         List<Task> objects) {
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
                        Task changeTask = (Task) cb.getTag();
                        changeTask.changeStatus();
                    }
                });
            } else {
                chk = (CheckBox) convertView.getTag();
            }
            Task current = taskList.get(position);
            chk.setText(current.getDescription());
            chk.setChecked(current.getStatus() == 1 ? true : false);
            chk.setTag(current);
            //Log.d("listener", String.valueOf(current.getId()));
            return convertView;
        }
    }
    private class EventAdapter extends ArrayAdapter<Event> {
        Context context;
        List<Event> eventList = new ArrayList<Event>();
        int layoutResourceId;
        public EventAdapter(Context context, int layoutResourceId,
                            List<Event> objects) {
            super(context, layoutResourceId, objects);
            this.layoutResourceId = layoutResourceId;
            this.eventList = objects;
            this.context = context;
        }
        /**
         * This method will Define what the view inside the list view will
         * finally look like Here we are going to code that the checkbox state
         * is the status of task and check box text is the task name
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txt = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.event_list_inner_view,
                        parent, false);
                txt = (TextView) convertView.findViewById(R.id.eventText);
                convertView.setTag(txt);
            } else {
                txt = (TextView) convertView.getTag();
            }
            Event current = eventList.get(position);
            txt.setText(current.getEventName() + "\n" + "Date: " + current.getdate() + "\n" +
                    "Start Time: " + current.getStartTime() + " End Time: " + current.getEndTime
                    ());

            txt.setTag(current);
            Log.d("listener", String.valueOf(current.getId()));
            return convertView;
        }
    }
}
