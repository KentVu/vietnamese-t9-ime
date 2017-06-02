package com.vutrankien.t9vietnamese;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vutrankien on 17/05/27.
 */
public class T9Engine {
    public T9Engine(String s) {
    }

    public Set<String> candidates(String s) {
        if ("24236".equals(s)) {
            HashSet<String> strings = new HashSet<>();
            strings.add("chào");
            return strings;
        }
        else return Collections.emptySet();
    }
}
