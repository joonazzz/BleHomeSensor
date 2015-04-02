package android.jsillanpaa.com.blehomesensor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class cls;
        if(false){
            cls = ScanActivity.class;
        }
        else{
            cls = GraphActivity.class;
        }

        Intent i = new Intent(MainActivity.this, cls);
        startActivity(i);
        finish();

    }

}
