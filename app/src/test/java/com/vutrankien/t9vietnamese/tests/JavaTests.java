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
        //new Normalizer()
        String chào = Normalizer.normalize("chào sắc huyền hỏi ngã nặng âươđ", Normalizer.Form
                .NFKD);
        System.out.println(chào);
        //System.out.print(chào.toCharArray());

        char[] charArray = chào.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Character c = charArray[i];
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                System.out.printf("%s %d %<X %s\n", c, Character.codePointAt(charArray, i),
                        Character
                        .getName(Character.codePointAt(charArray, i)));
            //} else {
            //    System.out.printf("%s %d\n", c, Character.codePointAt(charArray, i));
            //}
        }
    }
}
