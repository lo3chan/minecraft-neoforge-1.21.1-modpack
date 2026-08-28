/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.MapValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

class ObjectValueWriter
implements ValueWriter {
    static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

    @Override
    public boolean canWrite(Object value) {
        return true;
    }

    @Override
    public void write(Object value, WriterContext context) {
        LinkedHashMap<String, Object> to = new LinkedHashMap<String, Object>();
        Set<Field> fields = ObjectValueWriter.getFields(value.getClass());
        for (Field field : fields) {
            to.put(field.getName(), ObjectValueWriter.getFieldValue(field, value));
        }
        MapValueWriter.MAP_VALUE_WRITER.write(to, context);
    }

    @Override
    public boolean isPrimitiveType() {
        return false;
    }

    private static Set<Field> getFields(Class<?> cls) {
        LinkedHashSet<Field> fields = new LinkedHashSet<Field>(Arrays.asList(cls.getDeclaredFields()));
        while (cls != Object.class) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        ObjectValueWriter.removeConstantsAndSyntheticFields(fields);
        return fields;
    }

    private static void removeConstantsAndSyntheticFields(Set<Field> fields) {
        Iterator<Field> iterator = fields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            if ((!Modifier.isFinal(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) && !field.isSynthetic() && !Modifier.isTransient(field.getModifiers())) continue;
            iterator.remove();
        }
    }

    private static Object getFieldValue(Field field, Object o) {
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        Object value = null;
        try {
            value = field.get(o);
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        field.setAccessible(isAccessible);
        return value;
    }

    private ObjectValueWriter() {
    }
}

