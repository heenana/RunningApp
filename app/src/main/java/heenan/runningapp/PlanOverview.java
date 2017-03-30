package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import static android.R.interpolator.linear;


/**
 * Created by gotal on 3/21/2017.
 */

public class PlanOverview extends AppCompatActivity {

    private int days_total;
    private int num_weeks;
    private String race_name;
    private boolean[] days_progress;
    private String[] weekly_sets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        Bundle b = getIntent().getExtras();

        race_name = b.getString("race_name");
        num_weeks = Integer.parseInt(b.getString("num_weeks"));

        weekly_sets  = b.getStringArray("weekly_sets");

        days_total = num_weeks * 2; //Assumed 2 day per week


        String filedata = file_plan_reaser();

        setTitle(getString(R.string.plan_overview_title)+": "+ race_name +" - "+num_weeks+" Weeks");
        //Number of days shown in plan overview activity screen - hardcoded for now ****

        //Also hardcoded ****
        days_progress = new boolean[days_total];


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

    }

    private String file_plan_reaser(){
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

            String completed = days_progress[pressed] ? "Completed" : "To-Do";
            b.putString("completed", completed);
            b.putInt("day_number", (pressed+1));

            i.putExtras(b);
            startActivity(i);


        }
    }





}
