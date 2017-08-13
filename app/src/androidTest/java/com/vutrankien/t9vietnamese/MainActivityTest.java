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

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
    @Test
    public void whenFirstStartThenCreateDatabase() throws SnappydbException {
        Activity activity = mActivityRule.getActivity();
        DB snappydb = DBFactory.open(activity, "vi-VN");
        HashSet<String> candidates =  snappydb.get("24236" );//new HashSet<String>().getClass() get array of string

        //assertTrue(new HashSet<String>(candidates), "Jack Reacher");
        assertTrue((candidates).contains("chào"));
        snappydb.close();
    }

}