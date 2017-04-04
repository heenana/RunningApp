package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joshua on 3/28/2017.
 */

public class CustomizePlan extends AppCompatActivity {

    private Button race_week_buttons[];
    private boolean[] days_progress;
    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    // List that holds 3 maps, one for each week plan. Each map contains
    // the following values:
    // "race_name" e.g [10K], [Marathons]
    // "num_weeks" e.g. [6], [12]
    // "weekly_sets" e.g. ["sets_num:run_min:rest_min" ..... "sets_num:run_min:rest_min"]

    public List<Map<String, String[]>> refined_weekly_plans;
    String raceLength = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_plan);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_customize_plan);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        //Type of race selected
        raceLength = b.getString("race_type");

        refined_weekly_plans = individual_plan_breaker(b.getStringArray("week_options"));


        setTitle(raceLength + " " + getString(R.string.race_setup));


        Log.e("MaPVals 1:", Arrays.toString(refined_weekly_plans.toArray()));

//        Log.e("MaPVals 2:", refined_weekly_plans.get(i);


        //3 options provided for each race type
        race_week_buttons = new Button[3];

        final int[] BUTTON_IDS = {R.id.week_op1, R.id.week_op2, R.id.week_op3};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_week_buttons[i] = (RadioButton) findViewById(BUTTON_IDS[i]);


            String num_weeks = refined_weekly_plans.get(i).get("num_weeks")[0];

            Log.e("MaPVals: " + i, num_weeks);


//            Log.e("loop "+1, Arrays.toString(refined_weekly_plans.toArray()));

            race_week_buttons[i].setText(num_weeks);

//            race_week_buttons[i].setOnClickListener(new CustomizePlan.ButtonClickListener());
        }

        Button workout = (Button) findViewById(R.id.beginplan_button);
        workout.setOnClickListener(new CustomizePlan.ButtonClickListener());


    }

    //Used if any option on action bar is clicked on
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    // Methods in which takes the raw week options sxhedule string array
    // Workout Plans are then processed into an organized map
    // this returns three maps, one for each plan
    private List<Map<String, String[]>> individual_plan_breaker(String[] week_options) {

        List<Map<String, String[]>> all_processed_weeks = new ArrayList<Map<String, String[]>>();


        for (int week = 0; week < week_options.length; week++) {

            HashMap<String, String[]> processed_week = new HashMap<String, String[]>();

            String data[] = week_options[week].split(";");
            Log.e("Week " + week, Arrays.toString(data));

            processed_week.put("race_name", new String[]{data[0]});
            processed_week.put("num_weeks", new String[]{data[1]});
            processed_week.put("weekly_sets", data[2].split(":"));

            all_processed_weeks.add(processed_week);
        }

        return all_processed_weeks;

    }

    // Method used to create a file for the custom plan
    private String custom_plan_creator(Map<String, String[]> week_data, int days_perweek) {

        /**
         * PLAN OVERVIEW FORMAT
         RACE:
         RACE_NAME;WEEKS_TOTAL;DAYS_TOTAL;DAYS_COMPLETED;[WEEK1[WEEK2
         WEEK
         [WEEK_NUMBER:DAYS_TOTAL:DAYS_COMPlETED:(DAY1(DAY2)DAY3)....
         DAY
         (DAY_NUMBER,COMPLETED,SETS_NUMBER,RUN_TIME,REST_TIME,COORDINATE_1,....COORDINATE_N
         *
         */

        String race_name = week_data.get("race_name")[0];
        int num_weeks = Integer.parseInt(week_data.get("num_weeks")[0]);
        int days_total = num_weeks * days_perweek;
        String[] weekly_sets = week_data.get("weekly_sets");



        StringBuilder created_plan = new StringBuilder();

        //  {RACE_NAME,WEEKS_TOTAL,DAYS_TOTAL,DAYS_COMPLETED,
        created_plan.append(race_name + ";" + num_weeks + ";" + days_total + ";0;");

        for (int week = 0; week < num_weeks; week++) {

            // WEEK_NUMBER,DAYS_TOTAL,DAYS_COMPlETED,
            created_plan.append("[" + (week + 1) + ":" + days_perweek + ":0:");

            for (int day = 0; day < days_perweek; day++) {

                String[] set_data = weekly_sets[week].split(",");

                //(DAY_NUMBER,COMPLETED,SETS_NUMBER,RUN_TIME,REST_TIME,COORDINATE_1,....COORDINATE_N)
                created_plan.append("(" + (day + 1) + ",0," + weekly_sets[week]);
            }

        }

        String final_created_plan = created_plan.toString();

        Log.e("FINAL PLAN", final_created_plan);

        return final_created_plan;

    }


    // method creates a file (filename.text) that contains filedata (customized_plan)
    private void file_creator_custom_plan(String filename, String filedata) {
        //Writing a file...

        Log.e("filename is", filename);

        try {
            // catches IOException below
            final String TESTSTRING = new String(filedata);

       /* We have to use the openFileOutput()-method
       * the ActivityContext provides, to
       * protect your file from others and
       * This is done for security-reasons.
       * We chose MODE_WORLD_READABLE, because
       *  we have nothing to hide in our file */
            FileOutputStream fOut = openFileOutput(filename,
                    MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(TESTSTRING);

       /* ensure that everything is
        * really written out and close */
            osw.flush();
            osw.close();

    //Reading the file back...

       /* We have to use the openFileInput()-method
        * the ActivityContext provides.
        * Again for security reasons with
        * openFileInput(...) */

            FileInputStream fIn = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fIn);

        /* Prepare a char-Array that will
         * hold the chars we read back in. */
            char[] inputBuffer = new char[TESTSTRING.length()];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);

            // Check if we read back the same chars that we had written out
            boolean isTheSame = TESTSTRING.equals(readString);

            Log.i("File Reading stuff", "success = " + isTheSame);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    // Handles clicks on the number of weeks wanting to train
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            Bundle b = new Bundle();
            Intent i = new Intent(CustomizePlan.this, PlanOverview.class);
            String created_plan_data = new String();
            String filename = new String();

            RadioGroup weeks_option = (RadioGroup) findViewById(R.id.radio_weeks);
            RadioGroup days_option = (RadioGroup) findViewById(R.id.radio_daysperweek);


            int days_perweek = 0;

            switch(days_option.getCheckedRadioButtonId()){
                case R.id.radio_d1:
                    days_perweek = 1;
                break;
                case R.id.radio_d2:
                    days_perweek = 2;
                    break;
                case R.id.radio_d3:
                    days_perweek = 3;
                    break;
                case R.id.radio_d4:
                    days_perweek = 4;
                    break;
                case R.id.radio_d5:
                    days_perweek = 5;
                    break;
            }

            Log.e("DAYSPW", ""+days_perweek);


            Map<String, String[]> week_data = new HashMap<String, String[]>();

            switch (weeks_option.getCheckedRadioButtonId()) {
                case R.id.week_op1:
                    //Type of race and number of weeks


                    week_data = refined_weekly_plans.get(0);

                    created_plan_data = custom_plan_creator(week_data, days_perweek);
                    filename = week_data.get("race_name")[0]+".txt";
                    file_creator_custom_plan(filename, created_plan_data);


                    b.putString("race_name", week_data.get("race_name")[0]);
                    b.putString("num_weeks", week_data.get("num_weeks")[0]);
                    b.putStringArray("weekly_sets", week_data.get("weekly_sets"));
                    b.putBoolean("file_existed", false);

                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.week_op2:

                    week_data = refined_weekly_plans.get(1);
                    created_plan_data = custom_plan_creator(week_data, days_perweek);
                    filename = week_data.get("race_name")[0]+".txt";
                    file_creator_custom_plan(filename, created_plan_data);

                    b.putString("race_name", week_data.get("race_name")[0]);
                    b.putString("num_weeks", week_data.get("num_weeks")[0]);
                    b.putStringArray("weekly_sets", week_data.get("weekly_sets"));
                    b.putBoolean("file_existed", false);

                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.week_op3:

                    week_data = refined_weekly_plans.get(2);
                    created_plan_data = custom_plan_creator(week_data, days_perweek);
                    filename = week_data.get("race_name")[0]+".txt";
                    file_creator_custom_plan(filename, created_plan_data);

                    b.putString("race_name", week_data.get("race_name")[0]);
                    b.putString("num_weeks", week_data.get("num_weeks")[0]);
                    b.putStringArray("weekly_sets", week_data.get("weekly_sets"));
                    b.putBoolean("file_existed", false);

                    i.putExtras(b);
                    startActivity(i);

                    break;
            }
        }
    }
}

