package heenan.runningapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
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

import java.util.Arrays;

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

    private Location[] locations_array;


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



        Log.e("INSIDE DOC", "GOT DAY_DATA");
        Log.e("Day Data Lenght", ""+day_data.length);
        Log.e("DD contents", Arrays.toString(day_data));

        length = (Double.parseDouble(walk) + Double.parseDouble(run)) * Double.parseDouble(sets);

        setTitle(getString(R.string.day_overview_title) + " " + day_number + ": " + completed);
        int[] textview_ids = new int[] {R.id.run, R.id.walk, R.id.sets, R.id.length, R.id.distance, R.id.avg_walking_speed, R.id.avg_running_speed};


        if(day_data.length > 5) {

            locations_array = dd_toLocations();

            float[] calculations = get_distance_avg_speeds();

            distance = String.format("%.2f", calculations[0]);
            avg_rs = String.format("%.2f", calculations[1]);
            avg_ws = String.format("%.2f", calculations[2]);

        }


        String[] display_data = new String[] {"Run: "+run+" minutes", "Walk: "+walk+ "minutes",
                                "Sets: "+sets, "Total Workout Time: "+(int) length + "minutes",
                                "Distance: "+distance+" m", "Avg. Walking Speed: "+avg_ws +" m/s",
                                "Avg. Running Speed: "+avg_rs +" m/s"};

        TextView[] text_views = new TextView[textview_ids.length];

        for(int view = 0; view < textview_ids.length; view++){
            text_views[view] = (TextView)findViewById(textview_ids[view]);
            Log.e("MY VALUE IS", ""+view);
            Log.e(""+display_data.length, ""+text_views.length);
            text_views[view].setText(display_data[view]);
        }

    }

    private float[] get_distance_avg_speeds(){

        float[] distances = new float[locations_array.length -1];

        // the avg speeds are in meters per second!
        float[] to_return = new float[3]; // [distance, avg_running_speed, avg_walking_speed]

        float total = 0;
        float avg_ws = 0;
        float avg_rs = 0;

        for(int loc = 0; loc < locations_array.length - 1; loc++){

            float distance = locations_array[loc].distanceTo(locations_array[loc + 1]);


            Log.e("Loc "+loc+"distance", ""+distances[loc]);

            distances[loc] = distance;
            if(loc % 2 == 0){
                avg_rs += distance;
            } else {
                avg_ws += distance;
            }

            total += distance;
        }

        Log.e("total", ""+total);

        // avg running speed = total_running_distance / (num_sets * running_mins_per set * 60) = total_running_distance / total_running_secs
        avg_rs /= (Float.parseFloat(day_data[2]) * Float.parseFloat(day_data[3]) * 60);

        // avg walking speed = total_walking_distance / (num_sets * walkingmins_per set * 60) = total_walking_distance / total_walking_secs
        avg_ws /= (Float.parseFloat(day_data[2]) * Float.parseFloat(day_data[4]) * 60);

        to_return[0] = total;
        to_return[1] = avg_rs;
        to_return[2] = avg_ws;

        return to_return;
    }




    private Location[] dd_toLocations(){

        Location[] locations_toReturn = new Location[(day_data.length - 5) /2];

        Log.e("locations_toReturn size", ""+locations_toReturn.length);
        Log.e("day_data size", ""+day_data.length);

        int index_day_data = 5;

        for(int loc = 0; loc < locations_toReturn.length; loc++){
            Log.e("LOC is"+loc, "IndexDD is "+index_day_data);

            locations_toReturn[loc] = new Location("");

            double lat = Location.convert(day_data[index_day_data]);
            double lon = Location.convert(day_data[index_day_data + 1]);

            locations_toReturn[loc].setLatitude(lat);
            locations_toReturn[loc].setLongitude(lon);

            index_day_data += 2;

            Log.e("LOC "+loc+" Lat", Double.toString(locations_toReturn[loc].getLatitude()));
            Log.e("LOC "+loc+" Lon", Double.toString(locations_toReturn[loc].getLongitude()));
        }

        return locations_toReturn;

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
                Intent i;
                switch (id) {
                    case R.id.plan_overview:
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", 1);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
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
                        Intent intent = new Intent(DayOverviewCompleted.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
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