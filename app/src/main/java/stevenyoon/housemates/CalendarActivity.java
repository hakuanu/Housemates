package stevenyoon.housemates;
/**
 * Created by Mikael Mantis 3/16/16
 */
import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import stevenyoon.housemates.Event;
import java.util.ArrayList;
import android.app.AlertDialog.Builder;
import android.widget.EditText;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<Event> events = new ArrayList<>();
    public AlertDialog.Builder eventPrompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        /**
         * Sets all the values for the event to be added
         */
        LayoutInflater factory = LayoutInflater.from(this);

        final View textEventEntry = factory.inflate(R.layout.event_prompt, null);

        final EditText eventNameInput = (EditText) textEventEntry.findViewById(R.id.newEventName);
        final EditText eventDateInput = (EditText) textEventEntry.findViewById(R.id.newEventDate);
        final EditText eventTimeStartInput = (EditText) textEventEntry.findViewById(R.id
                .newEventTimeS);
        final EditText eventTimeEndInput = (EditText) textEventEntry.findViewById(R.id
                .newEventTimeE);
        final EditText eventClubInput = (EditText) textEventEntry.findViewById(R.id.newEventClub);
        final EditText eventDetailsInput = (EditText) textEventEntry.findViewById(R.id
                .newEventDetails);


        eventPrompt = new AlertDialog.Builder(this);
        eventPrompt.setTitle("Enter New Event");

        eventPrompt.setView(textEventEntry);

        eventPrompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String eventName = eventNameInput.getText().toString();
                String eventDate = eventDateInput.getText().toString();
                String eventTimeS = eventTimeStartInput.getText().toString();
                String eventTimeE = eventTimeEndInput.getText().toString();
                String eventClub = eventClubInput.getText().toString();
                String eventDetails = eventDetailsInput.getText().toString();
                System.out.println(events.size());
                events.add(new Event(eventDate, eventTimeS, eventTimeE, eventName, eventClub,
                        eventDetails));

            }
        });

        eventPrompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

    }

    public void addEvent(View v) {
        events.add(new Event("ugh", "UGH", "ugH", "UgH", "why", "hmm"));
        eventPrompt.show();

        System.out.println(events.size());
        for (int i = 0; i < events.size(); i++) {
            System.out.println(events.get(i).getEventName());
        }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {
            // Handle the camera action
        }
        else if (id == R.id.nav_tasks) {
            Intent i = new Intent(CalendarActivity.this, TasksActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_settings) {
            Intent i = new Intent(CalendarActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_messaging) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
