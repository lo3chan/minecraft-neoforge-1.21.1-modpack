/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriters;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

abstract class ArrayValueWriter
implements ValueWriter {
    ArrayValueWriter() {
    }

    protected static boolean isArrayish(Object value) {
        return value instanceof Collection || value.getClass().isArray();
    }

    @Override
    public boolean isPrimitiveType() {
        return false;
    }

    static boolean isArrayOfPrimitive(Object array) {
        Object first = ArrayValueWriter.peek(array);
        if (first != null) {
            ValueWriter valueWriter = ValueWriters.WRITERS.findWriterFor(first);
            return valueWriter.isPrimitiveType() || ArrayValueWriter.isArrayish(first);
        }
        return true;
    }

    protected Collection<?> normalize(Object value) {
        ArrayList<Object> collection;
        if (value.getClass().isArray()) {
            collection = new ArrayList<Object>(Array.getLength(value));
            for (int i = 0; i < Array.getLength(value); ++i) {
                Object elem = Array.get(value, i);
                collection.add(elem);
            }
        } else {
            collection = (ArrayList<Object>)value;
        }
        return collection;
    }

    private static Object peek(Object value) {
        if (value.getClass().isArray()) {
            if (Array.getLength(value) > 0) {
                return Array.get(value, 0);
            }
            return null;
        }
        Collection collection = (Collection)value;
        if (collection.size() > 0) {
            return collection.iterator().next();
        }
        return null;
    }
}

