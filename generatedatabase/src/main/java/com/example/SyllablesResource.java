package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by vutrankien on 17/07/02.
 */

class SyllablesResource {
    public String readLine() throws IOException {
        URL resource = SyllablesResource.class.getResource("syllables.txt");
        resource.getFile();
        InputStream inputStream = this.getClass().getResourceAsStream("syllables.txt");
        String s = new BufferedReader(
                new InputStreamReader(inputStream)
        ).readLine();
        return s;
    }
}
