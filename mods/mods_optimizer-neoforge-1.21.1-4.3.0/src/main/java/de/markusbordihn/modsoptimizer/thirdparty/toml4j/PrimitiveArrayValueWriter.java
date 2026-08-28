/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ArrayValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriters;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.util.Collection;

class PrimitiveArrayValueWriter
extends ArrayValueWriter {
    static final ValueWriter PRIMITIVE_ARRAY_VALUE_WRITER = new PrimitiveArrayValueWriter();

    @Override
    public boolean canWrite(Object value) {
        return PrimitiveArrayValueWriter.isArrayish(value) && PrimitiveArrayValueWriter.isArrayOfPrimitive(value);
    }

    @Override
    public void write(Object o, WriterContext context) {
        Collection<?> values = this.normalize(o);
        context.write('[');
        context.writeArrayDelimiterPadding();
        boolean first = true;
        ValueWriter firstWriter = null;
        for (Object value : values) {
            if (first) {
                firstWriter = ValueWriters.WRITERS.findWriterFor(value);
                first = false;
            } else {
                ValueWriter writer = ValueWriters.WRITERS.findWriterFor(value);
                if (writer != firstWriter) {
                    throw new IllegalStateException(context.getContextPath() + ": cannot write a heterogeneous array; first element was of type " + firstWriter + " but found " + writer);
                }
                context.write(", ");
            }
            ValueWriters.WRITERS.findWriterFor(value).write(value, context);
        }
        context.writeArrayDelimiterPadding();
        context.write(']');
    }

    private PrimitiveArrayValueWriter() {
    }

    public String toString() {
        return "primitive-array";
    }
}

