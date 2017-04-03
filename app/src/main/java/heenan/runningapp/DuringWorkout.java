package heenan.runningapp;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;

/**
 * Created by gotal on 3/31/2017.
 */

public class DuringWorkout  extends AppCompatActivity {

    private TextView[] timer_views;
    private double[] day_data;
    private double length;
    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //Variables that are used with the timer
    long time_left; //How much time needed left for the timer
    int task; //Current task number you are at
    double totalTasks; //Total tasks to be completed (sets * 2)
    int runWalkSwitch; //(run - 0) --- (walk - 1



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_workout);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_during_workout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
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

    private void timer_updater(){

        task = 0; //Current task number you are at
        totalTasks = day_data[2] * 2; //Total tasks to be completed (sets * 2)
        runWalkSwitch = 0; //(run - 0) -- (walk - 1)
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


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
            task = task + 1;
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
            }
        }
    }

}
