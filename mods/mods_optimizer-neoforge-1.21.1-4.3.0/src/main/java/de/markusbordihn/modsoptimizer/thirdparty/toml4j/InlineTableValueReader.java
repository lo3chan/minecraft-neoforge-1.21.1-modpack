/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Identifier;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReader;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReaders;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

class InlineTableValueReader
implements ValueReader {
    static final InlineTableValueReader INLINE_TABLE_VALUE_READER = new InlineTableValueReader();

    @Override
    public boolean canRead(String s) {
        return s.startsWith("{");
    }

    @Override
    public Object read(String s, AtomicInteger sharedIndex, Context context) {
        AtomicInteger line = context.line;
        int startLine = line.get();
        int startIndex = sharedIndex.get();
        boolean inKey = true;
        boolean inValue = false;
        boolean terminated = false;
        StringBuilder currentKey = new StringBuilder();
        HashMap<String, Object> results = new HashMap<String, Object>();
        Results.Errors errors = new Results.Errors();
        int i = sharedIndex.incrementAndGet();
        while (sharedIndex.get() < s.length()) {
            char c = s.charAt(i);
            if (inValue && !Character.isWhitespace(c)) {
                Object converted = ValueReaders.VALUE_READERS.convert(s, sharedIndex, context.with(Identifier.from(currentKey.toString(), context)));
                if (converted instanceof Results.Errors) {
                    errors.add((Results.Errors)converted);
                    return errors;
                }
                String currentKeyTrimmed = currentKey.toString().trim();
                Object previous = results.put(currentKeyTrimmed, converted);
                if (previous != null) {
                    errors.duplicateKey(currentKeyTrimmed, context.line.get());
                    return errors;
                }
                currentKey = new StringBuilder();
                inValue = false;
            } else if (c == ',') {
                inKey = true;
                inValue = false;
                currentKey = new StringBuilder();
            } else if (c == '=') {
                inKey = false;
                inValue = true;
            } else {
                if (c == '}') {
                    terminated = true;
                    break;
                }
                if (inKey) {
                    currentKey.append(c);
                }
            }
            i = sharedIndex.incrementAndGet();
        }
        if (!terminated) {
            errors.unterminated(context.identifier.getName(), s.substring(startIndex), startLine);
        }
        if (errors.hasErrors()) {
            return errors;
        }
        return results;
    }

    private InlineTableValueReader() {
    }
}

