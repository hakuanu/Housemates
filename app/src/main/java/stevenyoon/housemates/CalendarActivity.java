package stevenyoon.housemates;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Event> list;
    private ListView listItems;
    private EventAdapter adapt;
    public static ArrayList<Event> events = new ArrayList<>();

    public AlertDialog.Builder eventPrompt;
    String eventName;
    String eventDate;
    String eventTimeS;
    String eventTimeE;
    String eventClub;
    String eventDetails;
    Date startTime;
    Date endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        list = new ArrayList<Event>();
        listItems = (ListView) findViewById(R.id.listedEvents);
        adapt = new EventAdapter(this, R.layout.event_list_inner_view, list);
        listItems.setAdapter(adapt);
        setupListViewListener();
    }

    public void addEvent(View v) {

        //if (prompt!=null);

        /**
         * Sets all the values for the event to be added
         */

        LayoutInflater factory = LayoutInflater.from(this);

        final View textEventEntry = factory.inflate(R.layout.event_prompt, null, false);
        if (textEventEntry != null) {
            ViewGroup parent = (ViewGroup) textEventEntry.getParent();
            if (parent != null) {
                parent.removeView(textEventEntry);
            }
        }

        final Calendar myCalendar = Calendar.getInstance();

        final EditText eventNameInput = (EditText) textEventEntry.findViewById(R.id.newEventName);
        final EditText eventDateInput = (EditText) textEventEntry.findViewById(R.id.newEventDate);
        eventDateInput.setFocusable(false);
        final EditText eventTimeStartInput = (EditText) textEventEntry.findViewById(R.id
                .newEventTimeS);
        eventTimeStartInput.setFocusable(false);
        final EditText eventTimeEndInput = (EditText) textEventEntry.findViewById(R.id
                .newEventTimeE);
        eventTimeEndInput.setFocusable(false);
        final EditText eventClubInput = (EditText) textEventEntry.findViewById(R.id.newEventClub);
        final EditText eventDetailsInput = (EditText) textEventEntry.findViewById(R.id
                .newEventDetails);
        final Date currentDate = myCalendar.getTime();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Date d = myCalendar.getTime();
                if (d.before(currentDate)) {
                    System.out.println("test");
                    eventDateInput.setText("Pick a date after today");
                } else {
                    eventDateInput.setText(sdf.format(myCalendar.getTime()));
                }

            }

        };

        eventDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(textEventEntry.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener
                () {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                String myFormat = "h:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                startTime = myCalendar.getTime();

                eventTimeStartInput.setText(sdf.format(myCalendar.getTime()));
            }
        };

        eventTimeStartInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(textEventEntry.getContext(), timeStart, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener
                () {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                String myFormat = "h:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                endTime = myCalendar.getTime();
                eventTimeEndInput.setText(sdf.format(myCalendar.getTime()));
            }
        };

        eventTimeEndInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(textEventEntry.getContext(), timeEnd, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        eventPrompt = new AlertDialog.Builder(this);
        eventPrompt.setTitle("Enter New Event");

        eventPrompt.setView(textEventEntry);

        eventPrompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                eventName = eventNameInput.getText().toString();
                eventDate = eventDateInput.getText().toString();
                eventTimeS = eventTimeStartInput.getText().toString();
                eventTimeE = eventTimeEndInput.getText().toString();
                eventClub = eventClubInput.getText().toString();
                eventDetails = eventDetailsInput.getText().toString();
                events.add(new Event(eventDate, eventTimeS, eventTimeE, eventName, eventClub,
                        eventDetails));

                // displayEvents();
                adapt.add(events.get(Event.max_event_id - 1));
                adapt.notifyDataSetChanged();

            }
        });

        eventPrompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        AlertDialog prompt = eventPrompt.create();
        prompt.show();


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
        getMenuInflater().inflate(R.menu.calendar_activity, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(CalendarActivity.this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_calendar) {
            Intent i = new Intent(CalendarActivity.this, CalendarActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_tasks) {
            Intent i = new Intent(CalendarActivity.this, TasksActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_payment) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupListViewListener() {
        listItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        list.remove(pos);
                        // Refresh the adapter
                        adapt.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
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
