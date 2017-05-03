package heenan.runningapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.R.interpolator.linear;


/**
 * Created by gotal on 3/21/2017.
 */

public class PlanOverview extends AppCompatActivity {

    private int days_total;
    private int num_weeks;
    private String race_name;
    private boolean[] days_progress;
    private Map<String, String> trainning_data;
    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigation;
    private Button[] button_days;
    private boolean file_existed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_plan_overview);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        navBar();
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        race_name = b.getString("race_name");
        file_existed = b.getBoolean("file_existed");


        String filedata = file_plan_reader();
        trainning_data = file_data_organizer(filedata);
        num_weeks = Integer.parseInt(trainning_data.get("num_weeks"));
        days_total = Integer.parseInt(trainning_data.get("days_total"));
        days_progress = new boolean[days_total];

        for(int i = 0; i < days_total; i++){
            //Log.e("what is it??", trainning_data.get(""+(i+1)).split(",")[1]);
            days_progress[i] = trainning_data.get(""+(i+1)).split(",")[1].equals("1");
            //Log.e(""+i, ""+days_progress[i]);
        }

        setTitle(getString(R.string.plan_overview_title)+": "+ race_name +" - "+num_weeks+" Weeks");
        //Number of days shown in plan overview activity screen - hardcoded for now ****



        LinearLayout ll = (LinearLayout)findViewById(R.id.days_planoverview);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //Created buttons for entire plan (30 days for now) ***
        button_days = new Button[days_total];

        for(int i=0;i< days_total;i++){
            button_days[i] = new Button(this);

            button_days[i].setText("Day "+(i+1));
           // Log.d("index", ""+i);
            //Completed days are shown in green
            if(days_progress[i]) {
                button_days[i].setBackgroundColor(Color.rgb(50, 168, 54));
            }
            //Set listener for day button in order to go to next activity
            button_days[i].setId(i);
            button_days[i].setOnClickListener(new PlanOverview.ButtonClickListener());
            ll.addView(button_days[i], lp);

        }

        Log.e("TESTINGWRITER", map_to_string_converter());

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
                    case R.id.main_menu:
                        i = new Intent(PlanOverview.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.plan_overview:
                        Toast.makeText(PlanOverview.this, "Currently Viewing Plan Overview", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.next_workout:
                        getNextWorkout();
                        break;
                    case R.id.new_race:
                        Intent intent = new Intent(PlanOverview.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    public void getNextWorkout() {
        String get_plan = file_plan_reader();
        Log.e("This is the plan", get_plan);
        String[] initial_arr = get_plan.split(";");
        String[] get_day_plans = get_plan.split("\\(");
        int day_number = Integer.parseInt(initial_arr[3]);
        String day_data;
        if (day_number == 0) {
            day_data = get_day_plans[1];
        } else {
            day_data = get_day_plans[day_number];
        }
        String completed = "To-Do";
        boolean fromPlanOverview = false;
        Bundle bundle = new Bundle();
        Intent intent = new Intent(PlanOverview.this, DayOverview.class);
        bundle.putBoolean("fromPlanOverview", fromPlanOverview);
        bundle.putString("race_name", race_name);
        bundle.putString("completed", completed);
        bundle.putInt("day_number", day_number + 1);
        bundle.putString("day_data", day_data);
        Log.e("completed", completed);
        Log.e("day_number", "" + day_number);
        Log.e("day_data", day_data);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);

    }
    // this methood uses the race_name to access and read the
    // custumized created schedule plan file
    private String file_plan_reader(){
        String filename = race_name+".txt";
        Log.e("what is the race name", race_name);

        String filedata = new String();

        try {

            FileInputStream fIn = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();
            filedata = sb.toString();
            filedata = filedata.replace("\n", "").replace("\r", "");
            fIn.close();


        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Log.e("FileSize", ""+filedata.length());
        Log.e("FileData", filedata);

        return filedata;
    }

    // this method takes in the filedata from race_name.txt and
    // processes the string in otder to create a map with organized data
    private Map<String, String> file_data_organizer(String filedata){
        Map<String, String> processed_data = new TreeMap<String, String>();

        Log.e("FILEDATA ISSSS", filedata);
        String[] initial_arr = filedata.split(";");

        Log.e("INSIDE HEEERE", "RESPOND");
        Log.e("InitialArr is", Arrays.toString(initial_arr));

        processed_data.put("race_name", initial_arr[0]);
        processed_data.put("num_weeks", initial_arr[1]);
        processed_data.put("days_total", initial_arr[2]);
        processed_data.put("days_completed", initial_arr[3]);

        Log.e("HERE --- ", initial_arr[4]);

        String[] all_weeks_data = initial_arr[4].split("\\[");

        // create string of 0000s correspondoing to daystotal size

        String progress_days = new String(new char[Integer.parseInt(processed_data.get("days_total"))]).replace("\0", "0");



        int days_perweek = Integer.parseInt(processed_data.get("days_total")) / Integer.parseInt(processed_data.get("num_weeks"));
        Log.e("DAYS PER WEEK", ""+days_perweek);

        processed_data.put("days_progress", progress_days);

        for(int week = 1; week < all_weeks_data.length; week++){

            String[] single_week_data = all_weeks_data[week].split("\\(");
            processed_data.put("week"+week, single_week_data[0].substring(0,single_week_data[0].length() -1));

            for(int day = 1; day < single_week_data.length; day++){
                int current_day = ((week - 1) * days_perweek) + day;
                //Log.e("CURRENTDAY", ""+current_day);
                processed_data.put(""+current_day, single_week_data[day]);
            }
        }

        return processed_data;
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            Context context = getApplicationContext();
            CharSequence text = "NADA";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);

            Bundle b = new Bundle();
            Intent i = new Intent(PlanOverview.this, DayOverview.class);
            //Intent i = new Intent(PlanOverview.this, DataReader.class);

            int pressed = view.getId();

            String day_data = trainning_data.get(""+(pressed+1));
            String completed = days_progress[pressed] ? "Completed" : "To-Do";
            String race = race_name;
            boolean fromPlanOverview = true;
            b.putBoolean("fromPlanOverview", fromPlanOverview);
            b.putString("completed", completed);
            b.putInt("day_number", (pressed+1));
            b.putString("day_data", day_data);
            b.putString("race_name", race);

            if(days_progress[pressed]){
                i = new Intent(PlanOverview.this, DayOverviewCompleted.class);
                i.putExtras(b);
                startActivity(i);
            } else {
                i.putExtras(b);
                startActivityForResult(i, 1);
            }
        }
    }

    // method takes a training_data map and converts it into a string
    // this string will them be used to write a new file with
    // the  method file_updater_custom_plan
    private String map_to_string_converter(){

        StringBuilder file = new StringBuilder();
        file.append(race_name+";"+num_weeks+";"+days_total+";"+trainning_data.get("days_completed")+";");

        int curr_day = 1;

        for(int week = 1; week <= num_weeks; week++){
            file.append("["+trainning_data.get("week"+week)+":");

            int days_pw = days_total/num_weeks;

            while(curr_day <= days_total && days_pw > 0){
                file.append("("+trainning_data.get(""+curr_day));
                days_pw--;
                curr_day++;
            }

        }

        return file.toString();
    }


    // method creates a file (filename.text) that contains filedata (customized_plan)
    private void file_updater_custom_plan(String filename, String filedata) {
        //Writing a file...

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("PO: do i get here?", "plz");
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

                Bundle b = data.getExtras();

                int day_completed = data.getIntExtra("day_completed", 0);
                String[] gps_locations = data.getStringArrayExtra("gps_locations");


                if(day_completed > 0){

                    Log.e("PO OAR", "about to print gpslocations");
                    Log.e("gps size", ""+gps_locations.length);
                    Log.e("gps contents", Arrays.toString(gps_locations));

                    Log.e("Day Completed is", ""+day_completed);

                    Log.e("Map Contents", Arrays.toString(trainning_data.entrySet().toArray()));

                    // plan data update
                    days_progress[day_completed - 1] = true;

                    char[] my_days = trainning_data.get("days_progress").toCharArray();
                    my_days[day_completed - 1] = '1';

                    trainning_data.put("days_progress", String.valueOf(my_days));

                    Log.e("DAYSPROGRESS",trainning_data.get("days_progress"));

                    trainning_data.put("days_completed",
                            ""+(Integer.parseInt(trainning_data.get("days_completed" ))+1));

                    //week data update
                    int week = (int) Math.ceil(day_completed /(days_total/num_weeks)) + 1;

                    Log.e("value is", "week"+week);

                    String[] week_data = trainning_data.get("week"+week).split(":");
                    week_data[2] = ""+(Integer.parseInt(week_data[2])+1);
                    StringBuilder newWeekString = new StringBuilder();

                    newWeekString.append(week_data[0]);
                    for(int w = 1; w < week_data.length; w++){
                        newWeekString.append(":"+week_data[w]);
                    }

                    trainning_data.put("week"+week, newWeekString.toString());

                    // day data update
                    String[] day_data = trainning_data.get(""+day_completed).split(",");

                    day_data[1] = "1";
                    StringBuilder newDayString = new StringBuilder();
                    newDayString.append(day_data[0]);
                    for (int i = 1; i < day_data.length; i++){
                        Log.e(""+i, day_data[i]);
                        newDayString.append(","+day_data[i]);
                    }

                    // putting the gps_location coordinates inside the day_completed map keyset

                    for (int coord = 0; coord < gps_locations.length; coord++){
                        newDayString.append(","+gps_locations[coord]);
                    }

                    Log.e(""+day_completed, newDayString.toString());

                    trainning_data.put(""+day_completed, newDayString.toString());

                    button_days[day_completed - 1].setBackgroundColor(Color.rgb(50, 168, 54));

                    String update_file_string = map_to_string_converter();

                    Log.e(race_name+".txt", update_file_string);
                    file_updater_custom_plan(race_name+".txt", update_file_string);

                }

            }

        }
    }



}
