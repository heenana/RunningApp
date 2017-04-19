package heenan.runningapp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

/**
 * Created by gotal on 3/31/2017.
 */

public class DuringWorkout  extends AppCompatActivity {

    // GPS Variables
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<double[]> queried_gps_locations;

    private TextView[] timer_views;
    private double[] day_data;
    private double length;
    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigation;

    boolean goToPlanOverView = false;
    boolean goToNewRace = false;

    //Variables that are used with the timer
    long time_left; //How much time needed left for the timer
    int task; //Current task number you are at
    double totalTasks; //Total tasks to be completed (sets * 2)
    int runWalkSwitch; //(run - 0) -- (walk - 1)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_workout);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_during_workout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        navBar();
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        String[] temp_arr = b.getStringArray("day_data");

        day_data = new double[5];

        for(int i = 0; i < temp_arr.length; i++){
            day_data[i] = Double.parseDouble(temp_arr[i]);
        }

        length = b.getDouble("length");

        double sets = day_data[2];
        double run = day_data[3];
        double walk = day_data[4];



        TextView running = (TextView) findViewById(R.id.run);
        TextView walking = (TextView) findViewById(R.id.walk);
        TextView sets_num = (TextView) findViewById(R.id.sets);
        TextView total_length = (TextView) findViewById(R.id.length);

        timer_views = new TextView[]{(TextView) findViewById(R.id.timer_current_instruction),
                                    (TextView) findViewById(R.id.current_instruction),
                                    (TextView) findViewById(R.id.time_left)};

        running.setText("Run: " + run + " minutes");
        walking.setText("Walk: " + walk + " minutes");
        sets_num.setText("Sets: " + (int) sets);
        total_length.setText("Total Workout Time: " + (int) length + " minutes");




        // array list to keep track of the user's gps locations through time
        queried_gps_locations = new ArrayList<double[]>();
        timer_updater();


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
                        goToPlanOverView = true;
                        onBackPressed();
                        break;
                    case R.id.next_workout:
                        Toast.makeText(DuringWorkout.this, "Currently training for latest workout", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.history:
                        Toast.makeText(DuringWorkout.this, "History Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                    case R.id.new_race:
                        goToNewRace = true;
                        onBackPressed();
                        break;
                    case R.id.settings:Toast.makeText(DuringWorkout.this, "Settings Selected", Toast.LENGTH_SHORT).show();
                        // i = new Intent(MainActivity.this, DayOverview.class);
                        //startActivity(i);
                        break;
                }
                return false;
            }
        });
    }

    private void timer_updater(){

        task = 0; //Current task number you are at
        totalTasks = day_data[2] * 2; //Total tasks to be completed (sets * 2)
        runWalkSwitch = 0; //(run - 0) -- (walk - 1)
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TextView instruction = (TextView) findViewById(R.id.current_instruction);

        GPSTracker gps = new GPSTracker(this);

        //Set the current instruction
        if(runWalkSwitch == 0){
            instruction.setText("Run!");
        } else {
            instruction.setText("Walk!");
        }

        TimerLeft timerLeft = new TimerLeft((long)length*60000,1000);
        timerLeft.start();

        //If you have yet to reach the final task, move onto the next task
        if(task != totalTasks){
            time_left = (long) ((day_data[3 + runWalkSwitch] * 60000));
            v.vibrate(1000);
            //Create new timer and use time_left
            Timer next_instruction_timer = new Timer(time_left, 1000);
            next_instruction_timer.start();
            //Update the switch so you can correctly access the running or walking time
            if(runWalkSwitch == 0){
                runWalkSwitch = 1; //Walk
            } else {
                runWalkSwitch = 0; //Run
            }

            Log.e("GPS ENABLES?", "ABOUT TO CHECK");
            if(gps.canGetLocation()){
                Log.e("GPS IS ENABLED", "Thank God");

                double latitude = gps.getLatitude(); // returns latitude
                double longitude = gps.getLongitude(); // returns longitude

                queried_gps_locations.add(new double[]{latitude, longitude});





            } // gps enabled} // return boolean true/false
            task = task + 1;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                DuringWorkout.this);

        alertDialog.setNegativeButton("Yes, I'm a wimp..", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //moveTaskToBack(true);
                if (goToPlanOverView) {
                        goToPlanOverView = false;
                        Intent intent = new Intent(DuringWorkout.this,
                                PlanOverview.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                } else if (goToNewRace){
                    goToNewRace = false;
                    Intent intent = new Intent(DuringWorkout.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else {
                    finish();
                }
            }
        });
        alertDialog.setPositiveButton("No, I got this..", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(false);
            }
        });
        alertDialog.setMessage("Are you sure you want to go back? Current progress will be erased.");
        alertDialog.setTitle("Suck it up, you can do it!!!");
        alertDialog.show();
        moveTaskToBack(false);
    }

    //This class is for the time remaining timer
    private class TimerLeft extends CountDownTimer{
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimerLeft(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long second = (millisUntilFinished / 1000) % 60;
            long minute = (millisUntilFinished / (1000 * 60)) % 60;
            long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;

            String time = String.format("%02d:%02d:%02d", hour, minute, second);

            timer_views[2].setText(time);
        }

        @Override
        public void onFinish() {
            timer_views[2].setText("Done! You did a great job today!");
        }
    }


    private class Timer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {

            long second = (millisUntilFinished / 1000) % 60;
            long minute = (millisUntilFinished / (1000 * 60)) % 60;
            long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;

            String time = String.format("%02d:%02d:%02d", hour, minute, second);

            timer_views[0].setText(time);

        }

        @Override
        public void onFinish() {
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            TextView instruction = (TextView) findViewById(R.id.current_instruction);

            //Set the current instruction
            if(runWalkSwitch == 0){
                instruction.setText("Run!");
            } else {
                instruction.setText("Walk!");
            }

            //If you have yet to reach the final task, move onto the next task
            if(task != totalTasks) {
                time_left = (long) ((day_data[3 + runWalkSwitch] * 60000));
                v.vibrate(1000);
                //Create new timer and use time_left
                Timer next_instruction_timer = new Timer(time_left, 1000);
                next_instruction_timer.start();
                //Update the switch so you can correctly access the running or walking time
                if(runWalkSwitch == 0){
                    runWalkSwitch = 1; //Walk
                } else {
                    runWalkSwitch = 0; //Run
                }
                task = task + 1;
            } else {
                timer_views[0].setText("Done! You did a great job today!");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 1);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }

    public class GPSTracker extends Service implements LocationListener {

        private final Context mContext;

        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        } catch (SecurityException e) {
                            Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
                            showSettingsAlert();
                        }

                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            try {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            } catch (SecurityException e) {
                                Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
                                showSettingsAlert();
                            }

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            try {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            } catch (SecurityException e) {
                                Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
                                showSettingsAlert();
                            }

                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {

                                try {
                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                } catch (SecurityException e) {
                                    Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
                                    showSettingsAlert();
                                }

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         * */
        public void stopUsingGPS(){
            if(locationManager != null){
                try {
                    locationManager.removeUpdates(GPSTracker.this);
                } catch (SecurityException e) {
                    Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
                    showSettingsAlert();
                }
            }
        }

        /**
         * Function to get latitude
         * */
        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */
        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         * @return boolean
         * */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog
         * On pressing Settings button will lauch Settings Options
         * */
        public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

    }
}


