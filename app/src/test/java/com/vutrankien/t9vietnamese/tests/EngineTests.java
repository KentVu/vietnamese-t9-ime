package com.vutrankien.t9vietnamese.tests;

import com.vutrankien.t9vietnamese.T9Engine;

import org.junit.Test;

import java.util.Locale;
import java.util.Set;

/**
 * Created by vutrankien on 17/04/23.
 */

public class EngineTests {
    @Test
    public void t9test() {
        // new Locale.Builder().setLanguageTag("vi").build()
        Set<String> candiateStrings = new T9Engine("vi-VN").candidates("24236");
    }
}
