package com.vutrankien.t9vietnamese;

import android.app.Activity;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by vutrankien on 17/07/09.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

    @Test
    public void whenFirstStartThenCreateDatabase() throws SnappydbException {
        Activity activity = Robolectric.setupActivity(MainActivity.class);
        DB snappydb = DBFactory.open(activity);
        String 	 name   =  snappydb.get("name");
        int 	 age    =  snappydb.getInt("age");
        boolean  single =  snappydb.getBoolean("single");
        String[] books  =  snappydb.getArray("books", String.class);// get array of string

        assertEquals(name, "Jack Reacher");
        snappydb.close();
    }
}
