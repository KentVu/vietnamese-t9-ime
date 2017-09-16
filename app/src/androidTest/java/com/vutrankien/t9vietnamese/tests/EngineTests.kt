package com.vutrankien.t9vietnamese.tests

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.snappydb.SnappydbException
import com.vutrankien.t9vietnamese.MainActivity
import com.vutrankien.t9vietnamese.T9Engine
import com.vutrankien.t9vietnamese.getEngineFor

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.util.Arrays
import java.util.HashMap
import java.util.HashSet

import junit.framework.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class EngineTests {
//    @Rule
//    val mActivityRule = ActivityTestRule(MainActivity::class.java)
    lateinit var context: Context
    @Before
    fun setUp() {
//        context = mActivityRule.activity
        context = InstrumentationRegistry.getTargetContext()
    }

    @Test
    @Throws(SnappydbException::class)
    fun testNumToStringViVN() {
        // new Locale.Builder().setLanguageTag("vi").build()
        val testCases = HashMap<String, Array<String>>()
        testCases.put("24236", arrayOf("chào"))
        testCases.put("864", arrayOf("tôi"))
        val engine = context.getEngineFor("vi-VN")
        testEngineNumToString(testCases, engine)
        engine.close()
    }

    @Test
    @Throws(SnappydbException::class)
    fun testNumToStringEnUsLocale() {
        val testCases = HashMap<String, Array<String>>()
        testCases.put("43556", arrayOf("hello"))
        testCases.put("362867", arrayOf("doctor"))
        val engine = context.getEngineFor("en-US")
        testEngineNumToString(testCases, engine)
        engine.close()
    }

    private fun testEngineNumToString(testCases: Map<String, Array<String>>, engine: T9Engine) {
        for ((key, value) in testCases) {
            val candiateStrings = engine.candidates(key)
            for (cand in value) {
                assertTrue(
                        String.format("[%s] not containing [%s], all candidates:%s", key, cand, candiateStrings),
                        candiateStrings.contains(cand))
                //Arrays.asList(candiateStrings).contains(cand));
            }
        }
    }

    @Test
    fun testStringToNumStandardConfigurationViVN() {
        // TODO number to wordlist query

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {

    }
}
