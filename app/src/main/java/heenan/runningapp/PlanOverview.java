package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_plan_overview);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        race_name = b.getString("race_name");

        String filedata = file_plan_reader();
        trainning_data = file_data_organizer(filedata);
        num_weeks = Integer.parseInt(trainning_data.get("num_weeks"));
        days_total = Integer.parseInt(trainning_data.get("days_total"));
        days_progress = new boolean[days_total];


        setTitle(getString(R.string.plan_overview_title)+": "+ race_name +" - "+num_weeks+" Weeks");
        //Number of days shown in plan overview activity screen - hardcoded for now ****



        LinearLayout ll = (LinearLayout)findViewById(R.id.days_planoverview);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //Created buttons for entire plan (30 days for now) ***
        for(int i=0;i< days_total;i++){
            Button myButton = new Button(this);

            myButton.setText("Day "+(i+1));
            Log.d("index", ""+i);
            //Completed days are shown in green
            if(days_progress[i]) {
                myButton.setBackgroundColor(Color.rgb(50, 168, 54));
            }
            //Set listener for day button in order to go to next activity
            myButton.setId(i);
            myButton.setOnClickListener(new PlanOverview.ButtonClickListener());
            ll.addView(myButton, lp);

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

    // this methood uses the race_name to access and read the
    // custumized created schedule plan file
    private String file_plan_reader(){
        String filename = race_name+".txt";

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

        String[] initial_arr = filedata.split(";");

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
                Log.e("CURRENTDAY", ""+current_day);
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

            b.putString("completed", completed);
            b.putInt("day_number", (pressed+1));
            b.putString("day_data", day_data);

            if(days_progress[pressed]){
                i = new Intent(PlanOverview.this, DayOverviewCompleted.class);
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

                Log.e("HERE-----", ""+day_completed);

            }

        }
    }



}
