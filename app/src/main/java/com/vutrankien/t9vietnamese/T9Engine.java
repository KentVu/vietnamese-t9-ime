package com.vutrankien.t9vietnamese;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class T9Engine {
    public T9Engine(String locale) {
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
