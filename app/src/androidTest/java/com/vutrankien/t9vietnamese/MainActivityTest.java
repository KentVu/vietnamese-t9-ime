package com.vutrankien.t9vietnamese;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
    @Test
    public void whenFirstStartThenCreateDatabase() throws SnappydbException {
        Activity activity = mActivityRule.getActivity();
        DB snappydb = DBFactory.open(activity);
        String 	 name   =  snappydb.get("name");
        int 	 age    =  snappydb.getInt("age");
        boolean  single =  snappydb.getBoolean("single");
        String[] books  =  snappydb.getArray("books", String.class);// get array of string

        assertEquals(name, "Jack Reacher");
        snappydb.close();
    }

}