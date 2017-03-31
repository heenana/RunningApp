package heenan.runningapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gotal on 3/22/2017.
 */

public class DayOverview extends AppCompatActivity {

    private Button race_week_buttons[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_overview);

        Bundle b = getIntent().getExtras();

        int day_number = b.getInt("day_number");
        String completed = b.getString("completed");
        String day_data[] = b.getString("day_data").split(",");
        String sets = day_data[2];
        String run = day_data[3];
        String walk = day_data[4];
        int length = (Integer.parseInt(walk) + Integer.parseInt(run)) * Integer.parseInt(sets) + 10;

        setTitle(getString(R.string.day_overview_title) + " " + day_number + ": " + completed);

        TextView running = (TextView) findViewById(R.id.run);
        TextView walking = (TextView) findViewById(R.id.walk);
        TextView sets_num = (TextView) findViewById(R.id.sets);
        TextView total_length = (TextView) findViewById(R.id.length);


        running.setText("Run: " + run + " minutes");
        walking.setText("Walk: " + walk + " minutes");
        sets_num.setText("Sets: " + sets + " minutes");
        total_length.setText("Total Workout Time: " + length + " minutes");

        //3 options provided for each race type
        race_week_buttons = new Button[1];

        final int[] BUTTON_IDS = {R.id.begin_button};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_week_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);
            race_week_buttons[i].setOnClickListener(new DayOverview.ButtonClickListener());
        }

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


                    i.putExtras(b);
                    startActivity(i);

                    break;
            }
        }


    }
}
