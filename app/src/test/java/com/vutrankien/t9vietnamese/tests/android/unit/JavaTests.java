/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
