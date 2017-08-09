package com.vutrankien.t9vietnamese.tests;

import com.vutrankien.t9vietnamese.T9Engine;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertTrue;

public class EngineTests {
    @Test
    public void t9test() {
        // new Locale.Builder().setLanguageTag("vi").build()
        final HashMap<String, String[]> testCases = new HashMap<>();
        testCases.put("24236", new String[]{"chào"});
        testCases.put("864", new String[]{"tôi"});
        testEngine(testCases, new T9Engine("vi-VN"));
    }

    @Test
    public void testEnUsLocale() {
        final HashMap<String, String[]> testCases = new HashMap<>();
        testCases.put("43556", new String[]{"hello"});
        testCases.put("362867", new String[]{"doctor"});
        final T9Engine engine = new T9Engine("en-US");
        testEngine(testCases, engine);
    }

    private void testEngine(HashMap<String, String[]> testCases, T9Engine engine) {
        for (Map.Entry<String, String[]> testCase : testCases.entrySet()) {
            Set<String> candiateStrings = engine.candidates(testCase.getKey());
            for (String cand : testCase.getValue()) {
                assertTrue(
                        String.format("[%s] not containing [%s], all candidates:%s", testCase.getKey(), cand, candiateStrings),
                        candiateStrings.contains(cand));
            }
        }
    }
}
