package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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


        Bundle b = getIntent().getExtras();
        //Type of race selected
        raceLength = b.getString("race_type");

        refined_weekly_plans = individual_plan_breaker(b.getStringArray("week_options"));


        setTitle(raceLength + " " + getString(R.string.race_setup));









        //3 options provided for each race type
        race_week_buttons = new Button[3];

        final int[] BUTTON_IDS = {R.id.week_op1, R.id.week_op2, R.id.week_op3};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_week_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);
            race_week_buttons[i].setOnClickListener(new CustomizePlan.ButtonClickListener());
        }
    }


    // Methods in which takes the raw week options sxhedule string array
    // Workout Plans are then processed into an organized map
    // this returns three maps, one for each plan
    private List<Map<String, String[]>> individual_plan_breaker(String[]week_options ) {

        List<Map<String, String[]>> all_processed_weeks = new ArrayList<Map<String, String[]>>();


        for (int week = 0; week < week_options.length; week++) {

            HashMap<String, String[]> processed_week = new HashMap<String, String[]>();

            String data[] = week_options[week].split(";");
            Log.e("Week "+week, Arrays.toString(data));

            processed_week.put("race_name", new String[]{data[0]});
            processed_week.put("num_weeks", new String[]{data[1]});
            processed_week.put("weekly_sets", data[2].split(":"));

            all_processed_weeks.add(processed_week);
        }

        return all_processed_weeks;

    }


    // Handles clicks on the number of weeks wanting to train
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            Bundle b = new Bundle();
            Intent i = new Intent(CustomizePlan.this, PlanOverview.class);

            switch (view.getId()) {
                case R.id.week_op1:
                    //Type of race and number of weeks
                    b.putString("raceLength", raceLength);
                    b.putString("weeks", getString(R.string.week6));
                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.week_op2:

                    b.putString("raceLength", raceLength);
                    b.putString("weeks", getString(R.string.week8));
                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.week_op3:

                    b.putString("raceLength", raceLength);
                    b.putString("weeks", getString(R.string.week10));
                    i.putExtras(b);
                    startActivity(i);

                    break;
            }
        }
    }
}

