package heenan.runningapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by gotal on 3/21/2017.
 */

public class PlanOverview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        Bundle b = getIntent().getExtras();
        String race_type = b.getString("race_type");


        setTitle(getString(R.string.plan_overview_title)+": "+ race_type);

    }
}
