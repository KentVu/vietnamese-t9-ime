package com.vutrankien.t9vietnamese;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        T9KeyboardView inputView = (T9KeyboardView) findViewById(R.id.dialpad2);
        inputView.setKeyboard(new T9Keyboard(this, R.xml.t9));

        // Check database exist the right way
        // TODO Check if database exists
        // create database
        // TODO create database in engine
        try {
            DB snappydb = DBFactory.open(this);
            if (!snappydb.get("HELO").equals("On your mark, sir!")) {
                snappydb.put("HELO", "On your mark, sir!");
                // TODO generate database
                snappydb.put("name", "Jack Reacher");
                snappydb.putInt("age", 42);
                snappydb.putBoolean("single", true);
                snappydb.put("books", new String[]{"One Shot", "Tripwire", "61 Hours"});

                snappydb.close();
            }

        } catch (SnappydbException e) {
            // database not exists included?
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

    public void onBtnClick(View view) {
        new KLog("MainActivity").d("onBtnClick() btn=" + ((Button) view).getText());
    }
}
