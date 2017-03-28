package heenan.runningapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.R.interpolator.linear;


/**
 * Created by gotal on 3/21/2017.
 */

public class PlanOverview extends AppCompatActivity {

    private int days_total;
    private boolean[] days_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        Bundle b = getIntent().getExtras();
        String race_type = b.getString("race_type");


        setTitle(getString(R.string.plan_overview_title)+": "+ race_type);

        days_total = b.getInt("days_total");
        days_progress = b.getBooleanArray("days_progress");


        LinearLayout ll = (LinearLayout)findViewById(R.id.days_planoverview);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i=0;i< days_total;i++){
            Button myButton = new Button(this);

            myButton.setText("Day "+(i+1));
            Log.d("index", ""+i);
            if(days_progress[i]) {
                myButton.setBackgroundColor(Color.rgb(50, 168, 54));
            }
            myButton.setId(i);
            myButton.setOnClickListener(new PlanOverview.ButtonClickListener());
            ll.addView(myButton, lp);


        }

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
