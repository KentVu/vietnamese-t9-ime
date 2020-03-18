package com.vutrankien.t9vietnamese.tests.android.unit;

import android.annotation.SuppressLint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.text.Normalizer;

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
}
