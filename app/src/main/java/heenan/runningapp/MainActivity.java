package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button race_options_buttons[];
    private boolean[] days_progress; //Hard coded completed and not completed for now****
    public String[] weeks;
    //For navigation menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.choose_race);

        //To see navigation bar at top
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        race_options_buttons = new Button[5];

        final int[] BUTTON_IDS = {R.id.chose_5k, R.id.chose_10k, R.id.chose_15k, R.id.chose_halfmarathon, R.id.chose_marathon};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_options_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);
            race_options_buttons[i].setOnClickListener(new ButtonClickListener());
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

    //Method reads entire contents from the Race Plan Master
    //Returns Array of Strings - Each string is a specific race plan for a week
    public  String[] readAllPlanMaster(){

        String result;
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.raceplansmaster);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            result = new String(b);
        } catch (Exception e) {
            result = "Error: can't show file.";
            Log.e("Error Reading it...", "Error..");
        }

        String splitResuilt[] = result.split("\\n");

        return splitResuilt;
    }

    //Get the three week plans associated with race type that is chosen
    public String[] getWeekOptions(int index) {
        String [] plans = readAllPlanMaster();
        String[] week_options = new String[3];
        for (int i = index; i <= index + 2; i++) {
            week_options[i % 3] = plans[i];
        }
        return week_options;

    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            //Left Toast here just in case we wanted to use it later
            //int duration = Toast.LENGTH_SHORT;
            //Toast toast = Toast.makeText(context, text, duration);

            Bundle b = new Bundle();  //Used to pass parameters between activities
            Intent i = new Intent(MainActivity.this, CustomizePlan.class);

            Log.e("DID I GET HERE","PLEASE");
            DataReader testing_file = new DataReader();
            Log.e("failed here", "hope not");
            testing_file.readAllPlanMaster();
//            testing_file.testmethod();


            switch (view.getId()) {
                case R.id.chose_5k:
                    weeks = getWeekOptions(0);
                    Log.e("Week 1", weeks[0]);
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_5k));
                    b.putStringArray("week_options", weeks);


                    //days_progress = new boolean[] {true, false, false};

                    //b.putString("race_type",getString(R.string.race_5k));
                    //b.putInt("days_total", 3);
                    //b.putBooleanArray("days_progress", days_progress);

                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.chose_10k:
                    weeks = getWeekOptions(3);
                    Log.e("Week 1", weeks[0]);
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_10k));
                    b.putStringArray("week_options", weeks);

//                   days_progress = new boolean[] {true, true, false, false, false};
//
//                    b.putString("race_type",getString(R.string.race_10k));
//                    b.putInt("days_total", 5);
//                    b.putBooleanArray("days_progress", days_progress);

                    i.putExtras(b);
                    startActivity(i);
                    break;

                case R.id.chose_15k:
                    weeks = getWeekOptions(6);
                    Log.e("Third week", weeks[2]);
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_15k));
                    b.putStringArray("week_options", weeks);

                    i.putExtras(b);
                    startActivity(i);
                    break;
                case R.id.chose_halfmarathon:
                    weeks = getWeekOptions(9);
                    Log.e("Week 1", weeks[0]);
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_halfmarathon));
                    b.putStringArray("week_options", weeks);
                    i.putExtras(b);
                    startActivity(i);
                    break;

                case R.id.chose_marathon:
                    weeks = getWeekOptions(12);
                    Log.e("Week 1", weeks[0]);
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_marathon));
                    b.putStringArray("week_options", weeks);
                    i.putExtras(b);
                    startActivity(i);
                    break;

            }
        }
    }


}
