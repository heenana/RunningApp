package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Joshua on 3/28/2017.
 */

public class CustomizePlan extends AppCompatActivity {

    private Button race_week_buttons[];
    private boolean[] days_progress;
    String raceLength = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_plan);

        Bundle b = getIntent().getExtras();
        //Type of race selected
        raceLength = b.getString("race_type");

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

