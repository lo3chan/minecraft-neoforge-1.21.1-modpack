/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ObjectValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.PrimitiveArrayValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.TableArrayValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriters;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MapValueWriter
implements ValueWriter {
    static final ValueWriter MAP_VALUE_WRITER = new MapValueWriter();
    private static final Pattern REQUIRED_QUOTING_PATTERN = Pattern.compile("^.*[^A-Za-z\\d_-].*$");

    @Override
    public boolean canWrite(Object value) {
        return value instanceof Map;
    }

    @Override
    public void write(Object value, WriterContext context) {
        Map from = (Map)value;
        if (MapValueWriter.hasPrimitiveValues(from, context)) {
            context.writeKey();
        }
        for (Map.Entry entry : from.entrySet()) {
            Object key = entry.getKey();
            Object fromValue = entry.getValue();
            if (fromValue == null) continue;
            ValueWriter valueWriter = ValueWriters.WRITERS.findWriterFor(fromValue);
            if (valueWriter.isPrimitiveType()) {
                context.indent();
                context.write(MapValueWriter.quoteKey(key)).write(" = ");
                valueWriter.write(fromValue, context);
                context.write('\n');
                continue;
            }
            if (valueWriter != PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER) continue;
            context.setArrayKey(key.toString());
            context.write(MapValueWriter.quoteKey(key)).write(" = ");
            valueWriter.write(fromValue, context);
            context.write('\n');
        }
        for (Map.Entry key : from.keySet()) {
            ValueWriter valueWriter;
            Object fromValue = from.get(key);
            if (fromValue == null || (valueWriter = ValueWriters.WRITERS.findWriterFor(fromValue)) != this && valueWriter != ObjectValueWriter.OBJECT_VALUE_WRITER && valueWriter != TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER) continue;
            valueWriter.write(fromValue, context.pushTable(MapValueWriter.quoteKey(key)));
        }
    }

    @Override
    public boolean isPrimitiveType() {
        return false;
    }

    private static String quoteKey(Object key) {
        String stringKey = key.toString();
        Matcher matcher = REQUIRED_QUOTING_PATTERN.matcher(stringKey);
        if (matcher.matches()) {
            stringKey = "\"" + stringKey + "\"";
        }
        return stringKey;
    }

    private static boolean hasPrimitiveValues(Map<?, ?> values, WriterContext context) {
        for (Object key : values.keySet()) {
            ValueWriter valueWriter;
            Object fromValue = values.get(key);
            if (fromValue == null || !(valueWriter = ValueWriters.WRITERS.findWriterFor(fromValue)).isPrimitiveType() && valueWriter != PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER) continue;
            return true;
        }
        return false;
    }

    private MapValueWriter() {
    }
}

