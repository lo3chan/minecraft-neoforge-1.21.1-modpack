/*
 * Decompiled with CFR 0.152.
 */
package de.maxhenkel.sound_physics_remastered.configbuilder.custom;

import de.maxhenkel.sound_physics_remastered.configbuilder.custom.AbstractValueList;
import java.util.List;

public class IntegerList
extends AbstractValueList<Integer> {
    protected IntegerList(Integer ... values) {
        super(values);
    }

    protected IntegerList(List<Integer> values) {
        super(values);
    }

    public static IntegerList of(Integer ... values) {
        return new IntegerList(values);
    }

    public static IntegerList of(List<Integer> values) {
        return new IntegerList(values);
    }
}

