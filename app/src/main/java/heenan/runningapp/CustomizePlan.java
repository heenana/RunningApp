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



        Log.e("MaPVals 1:", Arrays.toString(refined_weekly_plans.toArray()));

//        Log.e("MaPVals 2:", refined_weekly_plans.get(i);


        //3 options provided for each race type
        race_week_buttons = new Button[3];

        final int[] BUTTON_IDS = {R.id.week_op1, R.id.week_op2, R.id.week_op3};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_week_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);


            String num_weeks = refined_weekly_plans.get(i).get("num_weeks")[0];

            Log.e("MaPVals: "+i, num_weeks);



//            Log.e("loop "+1, Arrays.toString(refined_weekly_plans.toArray()));

            race_week_buttons[i].setText(num_weeks);

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

    // Method used to create a file for the custom plan
    private String custom_plan_creator(int plan_number, int days_perweek){

        Map<String, String[]> week_data = refined_weekly_plans.get(plan_number);


        /**
         * PLAN OVERVIEW FORMAT
                 RACE:
                    {RACE_NAME,WEEKS_TOTAL,DAYS_TOTAL,DAYS_COMPLETED,[WEEK1],[WEEK2].... }
                WEEK
                    WEEK_NUMBER,DAYS_TOTAL,DAYS_COMPlETED,(DAY1),(DAY2),(DAY3)....]
                DAY
                    (DAY_NUMBER,COMPLETED,SETS_NUMBER,RUN_TIME,REST_TIME,COORDINATE_1,....COORDINATE_N)
         *
         */

        String race_name = week_data.get("race_name")[0];
        int num_weeks = Integer.parseInt(week_data.get("num_weeks")[0]);
        int days_total = num_weeks * days_perweek;
        String[] weekly_sets = week_data.get("weekly_sets");

        StringBuilder created_plan = new StringBuilder();

        //  {RACE_NAME,WEEKS_TOTAL,DAYS_TOTAL,DAYS_COMPLETED,
        created_plan.append("{"+race_name+","+num_weeks+","+days_total+",0");

        for(int week = 0; week < num_weeks ; week++){

            // WEEK_NUMBER,DAYS_TOTAL,DAYS_COMPlETED,
            created_plan.append(",["+(week+1)+","+days_perweek+",0");

            for(int day = 0; day < days_perweek ; day++){

                String[] set_data = weekly_sets[week].split(",");

                //(DAY_NUMBER,COMPLETED,SETS_NUMBER,RUN_TIME,REST_TIME,COORDINATE_1,....COORDINATE_N)
                created_plan.append(",("+(day+1)+",0,"+weekly_sets[week]+")");
            }

            created_plan.append("]");
        }

        created_plan.append("}");

        String final_created_plan = created_plan.toString();

        Log.e("FINAL PLAN",final_created_plan);

        return final_created_plan;

    }

    // Handles clicks on the number of weeks wanting to train
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            Bundle b = new Bundle();
            Intent i = new Intent(CustomizePlan.this, PlanOverview.class);


            Map<String, String[]> week_data = new HashMap<String, String[]>();

            switch (view.getId()) {
                case R.id.week_op1:
                    //Type of race and number of weeks

                    custom_plan_creator(0,2);

                    week_data = refined_weekly_plans.get(0);



                    b.putString("race_name", week_data.get("race_name")[0]);
                    b.putString("num_weeks", week_data.get("num_weeks")[0]);
                    b.putStringArray("weekly_sets", week_data.get("weekly_sets"));

                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.week_op2:

                    week_data = refined_weekly_plans.get(1);

                    b.putString("race_name", week_data.get("race_name")[0]);
                    b.putString("num_weeks", week_data.get("num_weeks")[0]);
                    b.putStringArray("weekly_sets", week_data.get("weekly_sets"));

                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.week_op3:


                    week_data = refined_weekly_plans.get(2);

                    b.putString("race_name", week_data.get("race_name")[0]);
                    b.putString("num_weeks", week_data.get("num_weeks")[0]);
                    b.putStringArray("weekly_sets", week_data.get("weekly_sets"));

                    i.putExtras(b);
                    startActivity(i);

                    break;
            }
        }
    }
}

