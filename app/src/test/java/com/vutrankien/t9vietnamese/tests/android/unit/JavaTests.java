package com.vutrankien.t9vietnamese.tests.android.unit;

import android.annotation.SuppressLint;

import com.stackoverflow.ResourcesHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Enumeration;

/**
 * Created by vutrankien on 17/06/03.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class JavaTests {

    @SuppressLint("NewApi")
    @Test
    public void normalizerTest() {
        String chào = Normalizer.normalize("chào sắc huyền hỏi ngã nặng âươđ ĂÂƯƠĐ",
                Normalizer.Form.NFKD);
        System.out.println(chào);

        char[] charArray = chào.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Character c = charArray[i];
            System.out.printf("%s %d %<X %s\n", c, Character.codePointAt(charArray, i),
                    Character
                    .getName(Character.codePointAt(charArray, i)));
        }
    }

    @Test
    public void getResourcesNames() throws IOException {
        Enumeration<URL> resources = JavaTests.class.getClassLoader().getResources("*");
        while (resources.hasMoreElements()) {
            System.out.println(resources.nextElement());
        }
    }

    @Test
    public void listResources() throws IOException, URISyntaxException {
        for (String res : ResourcesHelper.getResourceFiles("./")) {
            System.out.println(res);
        }
    }

    @Test
    public void listResources1() throws IOException, URISyntaxException {
        for (String res : ResourcesHelper.getFilenamesForDirnameFromCP("./")) {
            System.out.println(res);
        }
    }
}
