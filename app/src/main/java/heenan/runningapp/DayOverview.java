package heenan.runningapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by gotal on 3/22/2017.
 */

public class DayOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        Bundle b = getIntent().getExtras();

        int day_number = b.getInt("day_number");
        String completed = b.getString("completed");

        setTitle(getString(R.string.day_overview_title)+" "+day_number+": "+completed);




    }



}
