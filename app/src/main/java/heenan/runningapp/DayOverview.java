package heenan.runningapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by gotal on 3/22/2017.
 */

public class DayOverview extends AppCompatActivity {

    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigation;
    private Button race_week_buttons[];
    private String[] day_data;
    private double length;
    private int day_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_overview);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_day_overview);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);


        mDrawerLayout.addDrawerListener(mToggle);
        navBar();
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        day_number = b.getInt("day_number");
        String completed = b.getString("completed");
        day_data = b.getString("day_data").split(",");
        String sets = day_data[2];
        String run = day_data[3];
        String walk = day_data[4];
        length = (Double.parseDouble(walk) + Double.parseDouble(run)) * Double.parseDouble(sets);

        setTitle(getString(R.string.day_overview_title) + " " + day_number + ": " + completed);

        TextView running = (TextView) findViewById(R.id.run);
        TextView walking = (TextView) findViewById(R.id.walk);
        TextView sets_num = (TextView) findViewById(R.id.sets);
        TextView total_length = (TextView) findViewById(R.id.length);


        running.setText("Run: " + run + " minutes");
        walking.setText("Walk: " + walk + " minutes");
        sets_num.setText("Sets: " + sets);
        total_length.setText("Total Workout Time: " + (int) length + " minutes");

        //3 options provided for each race type
        race_week_buttons = new Button[1];

        final int[] BUTTON_IDS = {R.id.begin_button};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_week_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);
            race_week_buttons[i].setOnClickListener(new DayOverview.ButtonClickListener());
        }

    }

    //Used if any option on action bar is clicked on
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)) {
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
                        //Toast.makeText(DayOverview.this, "Plan Overview Selected", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", 1);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                        break;
                    case R.id.next_workout:
                        Toast.makeText(DayOverview.this, "Currently viewing latest workout", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.history:
                        Toast.makeText(DayOverview.this, "History Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                    case R.id.new_race:
                        Intent intent = new Intent(DayOverview.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        break;
                    case R.id.settings:Toast.makeText(DayOverview.this, "Settings Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                }
                return false;
            }
        });
    }

    // Handles clicks on the number of weeks wanting to train
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            Bundle b = new Bundle();
            Intent i = new Intent(DayOverview.this, DuringWorkout.class);
            String created_plan_data = new String();
            String filename = new String();


            switch (view.getId()) {
                case R.id.begin_button:
                    //Type of race and number of weeks

                    b.putStringArray("day_data", day_data);
                    b.putDouble("length", length);

                    i.putExtras(b);
                    startActivityForResult(i, 1);

                    break;
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("do i get here?", "plz");
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){


                Bundle b = data.getExtras();

                String[] gps_locations = data.getStringArrayExtra("gps_locations");

                Intent returnIntent = new Intent();


                returnIntent.putExtra("day_completed", day_number);
                returnIntent.putExtra("gps_locations", gps_locations);

                setResult(Activity.RESULT_OK,returnIntent);

                Log.e("go to plan overview", "plz");
                finish();

            }

        }
    }
}
