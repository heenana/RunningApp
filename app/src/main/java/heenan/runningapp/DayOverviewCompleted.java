package heenan.runningapp;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gotal on 4/3/2017.
 */

public class DayOverviewCompleted extends AppCompatActivity {

    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigation;
    private Button race_week_buttons[];
    private String[] day_data;
    private double length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_overview_completed);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_day_overview_completed);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        navBar();
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        int day_number = b.getInt("day_number");
        String completed = b.getString("completed");
        day_data = b.getString("day_data").split(",");
        String sets = day_data[2];
        String run = day_data[3];
        String walk = day_data[4];
        String distance = "N/A";
        String avg_ws = "N/A";
        String avg_rs = "N/A";

        length = (Double.parseDouble(walk) + Double.parseDouble(run)) * Double.parseDouble(sets) + 10;

        setTitle(getString(R.string.day_overview_title) + " " + day_number + ": " + completed);

        int[] textview_ids = new int[] {R.id.run, R.id.walk, R.id.sets, R.id.length, R.id.distance, R.id.avg_walking_speed, R.id.avg_running_speed};
        String[] display_data = new String[] {"Run: "+run+" minutes", "Walk: "+walk+ "minutes",
                                "Sets: "+sets, "Total Workout Time: "+(int) length + "minutes",
                                "Distance: "+distance+" km", "Avg. Walking Speed: "+avg_ws +" m/s",
                                "Avg. Running Speed: "+avg_rs +" m/s"};
        TextView[] text_views = new TextView[textview_ids.length];

        for(int view = 0; view < textview_ids.length; view++){
            text_views[view] = (TextView)findViewById(textview_ids[view]);
            Log.e("MY VALUE IS", ""+view);
            Log.e(""+display_data.length, ""+text_views.length);
            text_views[view].setText(display_data[view]);
        }

    }

    //Used if any option on action bar is clicked on
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    //Navigation menu - item on click
    private void navBar() {
        navigation = (NavigationView) findViewById(R.id.nav_menu);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                //Intent i;
                switch (id) {
                    case R.id.plan_overview:
                        Toast.makeText(DayOverviewCompleted.this, "Plan Overview Selected", Toast.LENGTH_SHORT).show();
                        //i = new Intent(MainActivity.this, PlanOverview.class);
                        //startActivity(i);
                        break;
                    case R.id.next_workout:
                        Toast.makeText(DayOverviewCompleted.this, "Next Workout Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                    case R.id.history:
                        Toast.makeText(DayOverviewCompleted.this, "History Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                    case R.id.new_race:
                        Toast.makeText(DayOverviewCompleted.this, "New Race Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                    case R.id.settings:Toast.makeText(DayOverviewCompleted.this, "Settings Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                }
                return false;
            }
        });
    }
}