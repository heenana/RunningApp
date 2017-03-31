package heenan.runningapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by gotal on 3/31/2017.
 */

public class DuringWorkout  extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_workout);

        Bundle b = getIntent().getExtras();


    }
}
