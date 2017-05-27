package com.vutrankien.t9vietnamese.tests;

import org.junit.Test;

import java.util.Locale;

/**
 * Created by vutrankien on 17/04/23.
 */

public class EngineTests {
    @Test
    public void t9test() {
        // new Locale.Builder().setLanguageTag("vi").build()
        new T9Engine("vi-VN", ).candidates("24236");
    }
}
