/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReader;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringValueReaderWriter
implements ValueReader,
ValueWriter {
    static final StringValueReaderWriter STRING_VALUE_READER_WRITER = new StringValueReaderWriter();
    private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\[uU](.{4})");
    private static final String[] specialCharacterEscapes = new String[93];

    @Override
    public boolean canRead(String s) {
        return s.startsWith("\"");
    }

    @Override
    public Object read(String s, AtomicInteger index, Context context) {
        int startIndex = index.incrementAndGet();
        int endIndex = -1;
        int i = index.get();
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (ch == '\"' && s.charAt(i - 1) != '\\') {
                endIndex = i;
                break;
            }
            i = index.incrementAndGet();
        }
        if (endIndex == -1) {
            Results.Errors errors = new Results.Errors();
            errors.unterminated(context.identifier.getName(), s.substring(startIndex - 1), context.line.get());
            return errors;
        }
        String raw = s.substring(startIndex, endIndex);
        s = this.replaceUnicodeCharacters(raw);
        if ((s = this.replaceSpecialCharacters(s)) == null) {
            Results.Errors errors = new Results.Errors();
            errors.invalidValue(context.identifier.getName(), raw, context.line.get());
            return errors;
        }
        return s;
    }

    String replaceUnicodeCharacters(String value) {
        Matcher unicodeMatcher = UNICODE_REGEX.matcher(value);
        while (unicodeMatcher.find()) {
            value = value.replace(unicodeMatcher.group(), new String(Character.toChars(Integer.parseInt(unicodeMatcher.group(1), 16))));
        }
        return value;
    }

    String replaceSpecialCharacters(String s) {
        for (int i = 0; i < s.length() - 1; ++i) {
            char ch = s.charAt(i);
            char next = s.charAt(i + 1);
            if (ch == '\\' && next == '\\') {
                ++i;
                continue;
            }
            if (ch != '\\' || next == 'b' || next == 'f' || next == 'n' || next == 't' || next == 'r' || next == '\"' || next == '\\') continue;
            return null;
        }
        return s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\t", "\t").replace("\\r", "\r").replace("\\\\", "\\").replace("\\/", "/").replace("\\b", "\b").replace("\\f", "\f");
    }

    @Override
    public boolean canWrite(Object value) {
        return value instanceof String || value instanceof Character || value instanceof URL || value instanceof URI || value instanceof Enum;
    }

    @Override
    public void write(Object value, WriterContext context) {
        context.write('\"');
        this.escapeUnicode(value.toString(), context);
        context.write('\"');
    }

    @Override
    public boolean isPrimitiveType() {
        return true;
    }

    private void escapeUnicode(String in, WriterContext context) {
        for (int i = 0; i < in.length(); ++i) {
            int codePoint = in.codePointAt(i);
            if (codePoint < specialCharacterEscapes.length && specialCharacterEscapes[codePoint] != null) {
                context.write(specialCharacterEscapes[codePoint]);
                continue;
            }
            context.write(in.charAt(i));
        }
    }

    private StringValueReaderWriter() {
    }

    public String toString() {
        return "string";
    }

    static {
        StringValueReaderWriter.specialCharacterEscapes[8] = "\\b";
        StringValueReaderWriter.specialCharacterEscapes[9] = "\\t";
        StringValueReaderWriter.specialCharacterEscapes[10] = "\\n";
        StringValueReaderWriter.specialCharacterEscapes[12] = "\\f";
        StringValueReaderWriter.specialCharacterEscapes[13] = "\\r";
        StringValueReaderWriter.specialCharacterEscapes[34] = "\\\"";
        StringValueReaderWriter.specialCharacterEscapes[92] = "\\\\";
    }
}

