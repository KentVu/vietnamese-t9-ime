package com.vutrankien.t9vietnamese.tests;

import android.annotation.SuppressLint;
import android.os.Build;

import org.junit.Test;

import java.text.Normalizer;

/**
 * Created by vutrankien on 17/06/03.
 */
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
