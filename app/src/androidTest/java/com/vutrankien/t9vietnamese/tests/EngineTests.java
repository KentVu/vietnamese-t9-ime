package com.vutrankien.t9vietnamese.tests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.snappydb.SnappydbException;
import com.vutrankien.t9vietnamese.MainActivity;
import com.vutrankien.t9vietnamese.T9Engine;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EngineTests {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
    private Context context = mActivityRule.getActivity();
    @Before
    public void setUp() {
        context = mActivityRule.getActivity();
    }
    @Test
    public void testNumToStringViVN() throws SnappydbException {
        // new Locale.Builder().setLanguageTag("vi").build()
        final HashMap<String, String[]> testCases = new HashMap<>();
        testCases.put("24236", new String[]{"chào"});
        testCases.put("864", new String[]{"tôi"});
        final T9Engine engine = new T9Engine(context, "vi-VN");
        testEngineNumToString(testCases, engine);
        engine.close();
    }

    @Test
    public void testNumToStringEnUsLocale() throws SnappydbException {
        final HashMap<String, String[]> testCases = new HashMap<>();
        testCases.put("43556", new String[]{"hello"});
        testCases.put("362867", new String[]{"doctor"});
        final T9Engine engine = new T9Engine(context, "en-US");
        testEngineNumToString(testCases, engine);
        engine.close();
    }

    private void testEngineNumToString(HashMap<String, String[]> testCases, T9Engine engine) {
        for (Map.Entry<String, String[]> testCase : testCases.entrySet()) {
            Set<String> candiateStrings = engine.candidates(testCase.getKey());
            for (String cand : testCase.getValue()) {
                assertTrue(
                        String.format("[%s] not containing [%s], all candidates:%s", testCase.getKey(), cand, candiateStrings),
                        candiateStrings.contains(cand));
                        //Arrays.asList(candiateStrings).contains(cand));
            }
        }
    }
    @Test
    public void testStringToNumStandardConfigurationViVN() {
        // TODO number to wordlist query

    }

    @After
    public void tearDown() throws Exception {

    }
}
