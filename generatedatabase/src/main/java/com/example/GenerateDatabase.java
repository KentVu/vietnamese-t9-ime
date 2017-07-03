package com.example;

import java.io.IOException;

public class GenerateDatabase {
    public static void main(String[] args) {
        try {
            System.out.print(new SyllablesResource().readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
