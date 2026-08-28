/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ArrayValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriters;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.util.Collection;

class TableArrayValueWriter
extends ArrayValueWriter {
    static final ValueWriter TABLE_ARRAY_VALUE_WRITER = new TableArrayValueWriter();

    @Override
    public boolean canWrite(Object value) {
        return TableArrayValueWriter.isArrayish(value) && !TableArrayValueWriter.isArrayOfPrimitive(value);
    }

    @Override
    public void write(Object from, WriterContext context) {
        Collection<?> values = this.normalize(from);
        WriterContext subContext = context.pushTableFromArray();
        for (Object value : values) {
            ValueWriters.WRITERS.findWriterFor(value).write(value, subContext);
        }
    }

    private TableArrayValueWriter() {
    }

    public String toString() {
        return "table-array";
    }
}

