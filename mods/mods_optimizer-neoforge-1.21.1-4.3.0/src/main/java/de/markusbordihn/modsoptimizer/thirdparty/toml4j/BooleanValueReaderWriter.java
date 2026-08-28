/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReader;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.util.concurrent.atomic.AtomicInteger;

class BooleanValueReaderWriter
implements ValueReader,
ValueWriter {
    static final BooleanValueReaderWriter BOOLEAN_VALUE_READER_WRITER = new BooleanValueReaderWriter();

    @Override
    public boolean canRead(String s) {
        return s.startsWith("true") || s.startsWith("false");
    }

    @Override
    public Object read(String s, AtomicInteger index, Context context) {
        Boolean b = (s = s.substring(index.get())).startsWith("true") ? Boolean.TRUE : Boolean.FALSE;
        int endIndex = b == Boolean.TRUE ? 4 : 5;
        index.addAndGet(endIndex - 1);
        return b;
    }

    @Override
    public boolean canWrite(Object value) {
        return Boolean.class.isInstance(value);
    }

    @Override
    public void write(Object value, WriterContext context) {
        context.write(value.toString());
    }

    @Override
    public boolean isPrimitiveType() {
        return true;
    }

    private BooleanValueReaderWriter() {
    }

    public String toString() {
        return "boolean";
    }
}

