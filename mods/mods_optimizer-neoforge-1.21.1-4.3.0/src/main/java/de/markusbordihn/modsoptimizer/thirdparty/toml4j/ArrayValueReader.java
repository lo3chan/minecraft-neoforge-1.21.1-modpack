/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReader;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReaders;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ArrayValueReader
implements ValueReader {
    static final ArrayValueReader ARRAY_VALUE_READER = new ArrayValueReader();

    @Override
    public boolean canRead(String s) {
        return s.startsWith("[");
    }

    @Override
    public Object read(String s, AtomicInteger index, Context context) {
        AtomicInteger line = context.line;
        int startLine = line.get();
        int startIndex = index.get();
        ArrayList<Object> arrayItems = new ArrayList<Object>();
        boolean terminated = false;
        boolean inComment = false;
        Results.Errors errors = new Results.Errors();
        int i = index.incrementAndGet();
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '#' && !inComment) {
                inComment = true;
            } else if (c == '\n') {
                inComment = false;
                line.incrementAndGet();
            } else if (!inComment && !Character.isWhitespace(c) && c != ',') {
                Object converted;
                if (c == '[') {
                    converted = this.read(s, index, context);
                    if (converted instanceof Results.Errors) {
                        errors.add((Results.Errors)converted);
                    } else if (!this.isHomogenousArray(converted, arrayItems)) {
                        errors.heterogenous(context.identifier.getName(), line.get());
                    } else {
                        arrayItems.add(converted);
                    }
                } else {
                    if (c == ']') {
                        terminated = true;
                        break;
                    }
                    converted = ValueReaders.VALUE_READERS.convert(s, index, context);
                    if (converted instanceof Results.Errors) {
                        errors.add((Results.Errors)converted);
                    } else if (!this.isHomogenousArray(converted, arrayItems)) {
                        errors.heterogenous(context.identifier.getName(), line.get());
                    } else {
                        arrayItems.add(converted);
                    }
                }
            }
            i = index.incrementAndGet();
        }
        if (!terminated) {
            errors.unterminated(context.identifier.getName(), s.substring(startIndex, s.length()), startLine);
        }
        if (errors.hasErrors()) {
            return errors;
        }
        return arrayItems;
    }

    private boolean isHomogenousArray(Object o, List<?> values) {
        return values.isEmpty() || values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
    }

    private ArrayValueReader() {
    }
}

