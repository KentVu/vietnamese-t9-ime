package com.example;

import java.io.IOException;

public class GenerateDatabase {
    public static void main(String[] args) {
        try {
            SyllablesResource syllablesResource = new SyllablesResource();
            do {
                String line = syllablesResource.readLine();
                System.out.println(line);
            } while(syllablesResource.hasNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
