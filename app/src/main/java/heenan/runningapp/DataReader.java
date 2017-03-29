package heenan.runningapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.InputStream;

/**
 * Created by Joshua on 3/27/2017.
 */

public class DataReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Method reads entire contents from the Race Plan Master
    //Returns Array of Strings - Each string is a specific race plan for a week
    public String[] readAllPlanMaster(){

        String result;
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.raceplansmaster);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            result = new String(b);
        } catch (Exception e) {
            result = "Error: can't show file.";
            Log.e("Error Reading it...", "Error..");
        }

        String splitResuilt[] = result.split("\\n");

        return splitResuilt;
    }

    //Method returns an array of Strings that contains instructions
    // for each week. Layout is (Sets, Run, Walk)
    public String[] getPlanOverview(String race, String week){

        String specificPlan[] = new String[Integer.parseInt(week)];

        String allRaces[] = readAllPlanMaster();

        for(int i = 0; i < allRaces.length; i++)
        {
            String data[] = allRaces[i].split(";");

            if(data[0] == race && data[1] == week)
            {
                specificPlan = data[2].split(":");
            }
        }

        return specificPlan;
    }

}
