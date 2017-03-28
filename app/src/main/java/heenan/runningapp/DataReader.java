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
        String result;
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.raceplansmaster);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            result = new String(b);
            Log.e("Printing from File ->", ""+result);
        } catch (Exception e) {
            // e.printStackTrace();
            result = "Error: can't show file.";
            Log.e("Error Reading it...", "Error..");
        }
    }



}
