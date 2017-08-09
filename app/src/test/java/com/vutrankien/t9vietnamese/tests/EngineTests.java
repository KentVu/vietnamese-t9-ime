package com.vutrankien.t9vietnamese.tests;

import com.vutrankien.t9vietnamese.T9Engine;

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by vutrankien on 17/04/23.
 */

public class EngineTests {
    @Test
    public void t9test() {
        // new Locale.Builder().setLanguageTag("vi").build()
        final HashMap<String, String[]> testCases = new HashMap<>();
        testCases.put("24236", new String[]{"chào"});
        testCases.put("864", new String[]{"tôi"});
        for (Map.Entry<String, String[]> testCase : testCases.entrySet()) {
            Set<String> candiateStrings = new T9Engine("vi-VN").candidates(testCase.getKey());
            for (String cand : testCase.getValue()) {
                candiateStrings.contains(cand);
            }
        }
    }
}
