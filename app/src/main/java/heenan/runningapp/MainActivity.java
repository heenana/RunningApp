package heenan.runningapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Button race_options_buttons[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.choose_race);

        race_options_buttons = new Button[2];


        final int[] BUTTON_IDS = {R.id.chose_5k, R.id.chose_10k};

        for (int i = 0; i < BUTTON_IDS.length; i++) {
            race_options_buttons[i] = (Button) findViewById(BUTTON_IDS[i]);
            race_options_buttons[i].setOnClickListener(new ButtonClickListener());
        }

    }


    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {


        public void onClick(View view) {

            Context context = getApplicationContext();
            CharSequence text = "NADA";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);

            switch (view.getId()) {
                case R.id.chose_5k:

                    text = "5k chosen!!";
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;
                case R.id.chose_10k:
                    // do stuff


                    text = "10k chosen!!";
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                    break;

            }
        }
    }


}
