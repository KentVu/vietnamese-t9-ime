package com.example;


import org.testng.annotations.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;


/**
 * Created by vutrankien on 17/07/02.
 */

public class DatabaseGeneratorTest {
    @Test
    public void getResourcesTest() throws IOException {
        assertNotNull(new SyllablesResource().readLine());
    }
}
