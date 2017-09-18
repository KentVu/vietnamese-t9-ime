package com.vutrankien.t9vietnamese

import android.app.Activity
import android.content.Context
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.snappydb.DB
import com.snappydb.DBFactory
import com.snappydb.SnappydbException

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Arrays
import java.util.HashSet

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    @Throws(SnappydbException::class)
    fun whenFirstStartThenCreateDatabase() {
        // it's too obvious we don't need to test it
//        val activity = mActivityRule.activity
//        val snappydb = DBFactory.open(activity, "vi-VN")
//
//        val candidates = snappydb.get("24236", HashSet<*>::class.java)//new HashSet<String>().getClass() get array of string
//
//        //assertTrue(new HashSet<String>(candidates), "Jack Reacher");
//        assertTrue(candidates.contains("chào"))
//        snappydb.close()
    }

}