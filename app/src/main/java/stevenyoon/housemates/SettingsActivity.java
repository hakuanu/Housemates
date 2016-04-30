package stevenyoon.housemates;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String group;

    @Override
    /*
    * onCreate method loads the toolbar, drawerlayout, sets the content view with the proper
    * layout, and sets the navigation view as well as displays the group's name
    * */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        group = getIntent().getStringExtra("group");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView groupname = (TextView) findViewById(R.id.groupName);
        groupname.setText(group);
    }

    /*
        Closes the drawer if its open or returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
     Inflate the menu; this adds items to the action bar if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
      Handles action bar item clicks
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    /*
        Handles navigation view item clicks, provides the option to navigate to the main,
        calendar, tasks, and splitwise activites.
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home){
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if(id == R.id.nav_calendar) {
            Intent i = new Intent(SettingsActivity.this, CalendarActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if (id == R.id.nav_tasks) {
            Intent i = new Intent(SettingsActivity.this, TasksActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }
        else if (id == R.id.nav_payment) {
            Intent i = new Intent(SettingsActivity.this, SplitwiseActivity.class);
            i.putExtra("group", group);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
