package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by vutrankien on 17/07/02.
 */

class SyllablesResource implements AutoCloseable {

    private final BufferedReader bufferedReader;

    private String nextLine;
    private String line;

    SyllablesResource() throws IOException {
        URL resource = SyllablesResource.class.getResource("syllables.txt");
        InputStream inputStream = this.getClass().getResourceAsStream("syllables.txt");
        bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
        );
        line = bufferedReader.readLine();
        nextLine = bufferedReader.readLine();
    }

    public String readLine() throws IOException {
        try {
            nextLine = bufferedReader.readLine();
            return line;
        } finally {
            line = nextLine;
        }
    }

    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public void close() throws Exception {
        bufferedReader.close();
    }
}
