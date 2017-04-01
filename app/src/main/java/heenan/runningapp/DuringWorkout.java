package heenan.runningapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by gotal on 3/31/2017.
 */

public class DuringWorkout  extends AppCompatActivity {

    private TextView[] timer_views;
    private double[] day_data;
    private double length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_during_workout);

        Bundle b = getIntent().getExtras();

        String[] temp_arr = b.getStringArray("day_data");

        day_data = new double[5];

        for(int i = 0; i < temp_arr.length; i++){
            day_data[i] = Double.parseDouble(temp_arr[i]);
        }

        length = b.getDouble("length");

        double sets = day_data[2];
        double run = day_data[3];
        double walk = day_data[4];



        TextView running = (TextView) findViewById(R.id.run);
        TextView walking = (TextView) findViewById(R.id.walk);
        TextView sets_num = (TextView) findViewById(R.id.sets);
        TextView total_length = (TextView) findViewById(R.id.length);

        timer_views = new TextView[]{(TextView) findViewById(R.id.timer_current_instruction),
                                    (TextView) findViewById(R.id.current_instruction),
                                    (TextView) findViewById(R.id.time_left)};

        running.setText("Run: " + run + " minutes");
        walking.setText("Walk: " + walk + " minutes");
        sets_num.setText("Sets: " + sets);
        total_length.setText("Total Workout Time: " + length + " minutes");

        timer_updater();


    }

    private void timer_updater(){

        int sets = 0;


        while(sets != day_data[2]){

            for(int rw = 0; rw < 2; rw++){

                long time_left = (long) ((day_data[3 + rw] * 60000));

                Timer next_instruction_timer = new Timer(time_left, 1000);
                next_instruction_timer.start();

                Log.e("Do I GET HERE?!?!?!?", ""+rw);
            }

            Log.e("Looping sets", ""+sets);
            sets++;
        }



    }


    private class Timer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            long second = (millisUntilFinished / 1000) % 60;
            long minute = (millisUntilFinished / (1000 * 60)) % 60;
            long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;

            String time = String.format("%02d:%02d:%02d", hour, minute, second);

            timer_views[0].setText(time);

        }

        @Override
        public void onFinish() {
            timer_views[0].setText("Done!");

        }
    }

}
