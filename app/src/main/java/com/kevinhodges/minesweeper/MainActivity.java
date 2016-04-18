package com.kevinhodges.minesweeper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI Declarations///////////////////////////////////////////////////////////
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///////////////////////////////////////////////////////////////////////////
    }

    // Google recommends the use of the Toolbar in place of the action bar to support older devices.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.new_game) {
            return true;
        }

        if (id == R.id.give_up) {
            return true;
        }

        if (id == R.id.stats) {
            return true;
        }

        if (id == R.id.about) {
            return true;
        }

        if (id == R.id.faq) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
