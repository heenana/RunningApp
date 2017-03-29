package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button race_options_buttons[];
    private boolean[] days_progress; //Hard coded completed and not completed for now****

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.choose_race);

        race_options_buttons = new Button[5];

        final int[] BUTTON_IDS = {R.id.chose_5k, R.id.chose_10k, R.id.chose_15k, R.id.chose_halfmarathon, R.id.chose_marathon};
        //Assigning button listener for each button id
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_options_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);
            race_options_buttons[i].setOnClickListener(new ButtonClickListener());
        }

    }


    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View view) {

            //Left Toast here just in case we wanted to use it later
            //int duration = Toast.LENGTH_SHORT;
            //Toast toast = Toast.makeText(context, text, duration);

            Bundle b = new Bundle();  //Used to pass parameters between activities
            Intent i = new Intent(MainActivity.this, CustomizePlan.class);

            switch (view.getId()) {
                case R.id.chose_5k:
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_5k));
                    //days_progress = new boolean[] {true, false, false};

                    //b.putString("race_type",getString(R.string.race_5k));
                    //b.putInt("days_total", 3);
                    //b.putBooleanArray("days_progress", days_progress);

                    i.putExtras(b);
                    startActivity(i);

                    break;
                case R.id.chose_10k:
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_10k));

//                   days_progress = new boolean[] {true, true, false, false, false};
//
//                    b.putString("race_type",getString(R.string.race_10k));
//                    b.putInt("days_total", 5);
//                    b.putBooleanArray("days_progress", days_progress);

                    i.putExtras(b);
                    startActivity(i);
                    break;
                case R.id.chose_15k:
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_15k));
                    i.putExtras(b);
                    startActivity(i);
                    break;
                case R.id.chose_halfmarathon:
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_halfmarathon));
                    i.putExtras(b);
                    startActivity(i);
                    break;

                case R.id.chose_marathon:
                    //Passing selected race to next activity CustomizePlan
                    b.putString("race_type",getString(R.string.race_marathon));
                    i.putExtras(b);
                    startActivity(i);
                    break;

            }
        }
    }


}
