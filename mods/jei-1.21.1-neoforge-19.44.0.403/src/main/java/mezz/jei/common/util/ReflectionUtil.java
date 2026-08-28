/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.common.collect.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReflectionUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Table<Class<?>, Class<?>, List<Field>> cache = Table.hashBasedTable();

    public <T> Stream<T> getFieldWithClass(Object object, Class<? extends T> fieldClass) {
        return this.getFieldsCached(object, fieldClass).flatMap(field -> ReflectionUtil.getFieldValue(object, field, fieldClass).stream());
    }

    private static <T> Optional<T> getFieldValue(Object object, Field field, Class<? extends T> fieldClass) {
        Object fieldValue;
        try {
            fieldValue = field.get(object);
        }
        catch (IllegalAccessException e) {
            LOGGER.error("Failed to access field '{}' for class {}", (Object)field.getName(), object.getClass(), (Object)e);
            return Optional.empty();
        }
        if (fieldClass.isInstance(fieldValue)) {
            T cast = fieldClass.cast(fieldValue);
            return Optional.of(cast);
        }
        return Optional.empty();
    }

    private Stream<Field> getFieldsCached(Object object, Class<?> fieldClass) {
        return this.cache.computeIfAbsent(fieldClass, object.getClass(), () -> ReflectionUtil.getFieldUncached(object, fieldClass).toList()).stream();
    }

    private static Stream<Field> getFieldUncached(Object object, Class<?> fieldClass) {
        return ReflectionUtil.getAllFields(object).filter(field -> fieldClass.isAssignableFrom(field.getType())).mapMulti((field, mapper) -> {
            try {
                field.setAccessible(true);
                mapper.accept(field);
            }
            catch (SecurityException | InaccessibleObjectException e) {
                LOGGER.error("Failed to access field '{}' for class {}", (Object)field.getName(), object.getClass(), (Object)e);
            }
        });
    }

    private static Stream<Field> getAllFields(Object object) {
        ArrayList classes = new ArrayList();
        for (Class<?> objectClass = object.getClass(); objectClass != Object.class; objectClass = objectClass.getSuperclass()) {
            classes.add(objectClass);
        }
        return classes.stream().flatMap(c -> {
            try {
                Field[] fields = c.getDeclaredFields();
                return Arrays.stream(fields);
            }
            catch (LinkageError | SecurityException e) {
                LOGGER.error("Failed to access fields for class {}", object.getClass(), (Object)e);
                return Stream.of(new Field[0]);
            }
        });
    }
}

