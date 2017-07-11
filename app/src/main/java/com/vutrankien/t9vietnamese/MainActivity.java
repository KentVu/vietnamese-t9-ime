package com.vutrankien.t9vietnamese;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.File;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Check database exist
        if (!new File(getString(R.string.database_name)).exists()) {
            // create database
            try {
                DB snappydb = DBFactory.open(this);
                snappydb.put("name", "Jack Reacher");
                snappydb.putInt("age", 42);
                snappydb.putBoolean("single", true);
                snappydb.put("books", new String[]{"One Shot", "Tripwire", "61 Hours"});

                snappydb.close();

            } catch (SnappydbException e) {
                TextView textView = (TextView) findViewById(R.id.text);
                int color;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    color = getColor(android.R.color.holo_red_dark);
                } else {
                    //noinspection deprecation
                    color = getResources().getColor(android.R.color.holo_red_dark);
                }
                textView.setTextColor(color);
                textView.setText(getString(R.string.oops, e
                        .getMessage()));
            }
        }
    }
}
