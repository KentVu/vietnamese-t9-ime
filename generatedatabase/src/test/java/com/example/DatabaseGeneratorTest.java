package com.example;


import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by vutrankien on 17/07/02.
 */

public class DatabaseGeneratorTest {

    @Test
    public void getResourcesTest() throws IOException {
        String s = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getResourceAsStream("/test/com/example/syllables.txt"))
        ).readLine();
        System.out.println(s);
    }
    @Test
    public void getResourcesTest2() throws IOException {
        String file = "com/example/syllables.txt";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
        System.out.print(new BufferedReader(inputStreamReader).readLine());
    }
    @Test
    public void getResourcesTest3() throws IOException {
        String s = DataStub.readFile(DataStub.BASE_PATH+"com/example/syllables.txt");
        System.out.print(s.substring(0, 10000));
    }

    public static final class DataStub {
        // https://stackoverflow.com/a/33057561/1562087
        private static final String BASE_PATH = resolveBasePath(); // e.g. "./mymodule/src/test/resources/";

        private DataStub() {
            //no instances
        }
        private static String resolveBasePath() {
            final String path = "./generatedatabase/src/test/resources/";
            String[] list = new File("./").list();
            if (Arrays.asList(list).contains("generatedatabase")) {
                System.out.println(Arrays.toString(list));
                return path; // version for call unit tests from Android Studio
            }
            return "../" + path; // version for call unit tests from terminal './gradlew test'
        }

        /**
         * Reads file content and returns string.
         */
        public static String readFile(@NonNull final String path) throws IOException {
            final StringBuilder sb = new StringBuilder();
            String strLine;
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
                while ((strLine = reader.readLine()) != null) {
                    sb.append(strLine);
                }
            } catch (final IOException ignore) {
                //ignore
            }
            return sb.toString();
        }
    }
}
