/*
 * Decompiled with CFR 0.152.
 */
package com.sonicether.soundphysics.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigUtils {
    public static <T extends Comparable<T>, U> Map<T, U> sortMap(Map<T, U> map) {
        ArrayList<Map.Entry<T, U>> entryList = new ArrayList<Map.Entry<T, U>>(map.entrySet());
        entryList.sort(Map.Entry.comparingByKey());
        LinkedHashMap sorted = new LinkedHashMap();
        for (Map.Entry entry : entryList) {
            sorted.put((Comparable)entry.getKey(), entry.getValue());
        }
        return sorted;
    }
}

