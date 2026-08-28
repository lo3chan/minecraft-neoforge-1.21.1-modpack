/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.StringValueReaderWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReader;
import java.util.concurrent.atomic.AtomicInteger;

class MultilineStringValueReader
implements ValueReader {
    static final MultilineStringValueReader MULTILINE_STRING_VALUE_READER = new MultilineStringValueReader();

    @Override
    public boolean canRead(String s) {
        return s.startsWith("\"\"\"");
    }

    @Override
    public Object read(String s, AtomicInteger index, Context context) {
        AtomicInteger line = context.line;
        int startLine = line.get();
        int originalStartIndex = index.get();
        int startIndex = index.addAndGet(3);
        int endIndex = -1;
        if (s.charAt(startIndex) == '\n') {
            startIndex = index.incrementAndGet();
            line.incrementAndGet();
        }
        int i = startIndex;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\n') {
                line.incrementAndGet();
            } else if (c == '\"' && s.length() > i + 2 && s.charAt(i + 1) == '\"' && s.charAt(i + 2) == '\"') {
                endIndex = i;
                index.addAndGet(2);
                break;
            }
            i = index.incrementAndGet();
        }
        if (endIndex == -1) {
            Results.Errors errors = new Results.Errors();
            errors.unterminated(context.identifier.getName(), s.substring(originalStartIndex), startLine);
            return errors;
        }
        s = s.substring(startIndex, endIndex);
        s = s.replaceAll("\\\\\\s+", "");
        s = StringValueReaderWriter.STRING_VALUE_READER_WRITER.replaceUnicodeCharacters(s);
        s = StringValueReaderWriter.STRING_VALUE_READER_WRITER.replaceSpecialCharacters(s);
        return s;
    }

    private MultilineStringValueReader() {
    }
}

