package stevenyoon.housemates;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String group;

    private List<Task> list;
    private ArrayAdapter<Task> itemsAdapter;
    private ListView listItems;
    private MyAdapter adapt;
    private ValueEventListener vel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
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

        list = new ArrayList<Task>();
        adapt = new MyAdapter(this, R.layout.list_inner_view, list);
        listItems = (ListView) findViewById(R.id.listedItems);
        //itemsAdapter = new ArrayAdapter<Task>(this,
        //        android.R.layout.simple_list_item_1, list);
        //itemsAdapter.add(new Task());
        listItems.setAdapter(adapt); //itemsAdapter
        setupListViewListener();
        loadTasksFromDB();
    }

    private void loadTasksFromDB(){
        Firebase ref = new Firebase("https://dazzling-torch-3636.firebaseio.com");
        ref = ref.child("groups").child(group).child("tasks");
        ref.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                String desc = (String)snapshot.child("description").getValue();
                String status = (String)snapshot.child("status").getValue();
                Task  task = new Task(desc, Integer.parseInt(status) , snapshot.getKey());

                adapt.add(task);
                adapt.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                System.out.println("The read failed: ");
            }
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                System.out.println("The read failed: ");
            }
            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                System.out.println("The read failed: ");
            }

        });
    }

    public void addTask(View v) {
        EditText t = (EditText) findViewById(R.id.newItem);
        String s = t.getText().toString();

        if(s.equalsIgnoreCase("")) {
            Toast.makeText(this, "Empty task not added", Toast.LENGTH_SHORT).show();

        }
        else {
            Map<String, String> firebaseTask = new HashMap<String, String>();
            firebaseTask.put("description", s);
            firebaseTask.put("status", "0");
            Firebase ref = new Firebase("https://dazzling-torch-3636.firebaseio.com");
            ref.child("groups").child(group).child("tasks").push().setValue(firebaseTask);
            t.setText("");
        }
    }

    private void setupListViewListener() {
        listItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        Task t = list.get(pos);
                        Firebase ref = new Firebase("https://dazzling-torch-3636.firebaseio.com");
                        ref=ref.child("groups").child(group).child("tasks").child(t.getId());
                        ref.removeValue();
                        list.remove(pos);
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
        getMenuInflater().inflate(R.menu.tasks_menu, menu);
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

        if (id == R.id.nav_home){
            Intent i = new Intent(TasksActivity.this, MainActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if(id == R.id.nav_calendar) {
            Intent i = new Intent(TasksActivity.this, CalendarActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if (id == R.id.nav_payment) {
            Intent i = new Intent(TasksActivity.this, SplitwiseActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_settings) {
            Intent i = new Intent(TasksActivity.this, SettingsActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }

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
                        Firebase ref = new Firebase("https://dazzling-torch-3636.firebaseio.com");
                        ref = ref.child("groups").child(group).child("tasks").child(changeTask.getId()).child("status");
                        ref.setValue(Integer.toString(changeTask.getStatus()));
                    }
                });
            } else {
                chk = (CheckBox) convertView.getTag();
            }
            Task current = taskList.get(position);
            chk.setText(current.getDescription());
            chk.setChecked(current.getStatus() == 1 ? true : false);
            chk.setTag(current);
            Log.d("listener", String.valueOf(current.getId()));
            return convertView;
        }
    }
}