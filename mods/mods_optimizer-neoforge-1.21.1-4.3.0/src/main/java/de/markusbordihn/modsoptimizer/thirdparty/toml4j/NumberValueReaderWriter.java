/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Results;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueReader;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.WriterContext;
import java.util.concurrent.atomic.AtomicInteger;

class NumberValueReaderWriter
implements ValueReader,
ValueWriter {
    static final NumberValueReaderWriter NUMBER_VALUE_READER_WRITER = new NumberValueReaderWriter();

    NumberValueReaderWriter() {
    }

    @Override
    public boolean canRead(String s) {
        char firstChar = s.charAt(0);
        return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar);
    }

    @Override
    public Object read(String s, AtomicInteger index, Context context) {
        boolean signable = true;
        boolean dottable = false;
        boolean exponentable = false;
        boolean terminatable = false;
        boolean underscorable = false;
        String type = "";
        StringBuilder sb = new StringBuilder();
        int i = index.get();
        while (i < s.length()) {
            boolean notLastChar;
            char c = s.charAt(i);
            boolean bl = notLastChar = s.length() > i + 1;
            if (Character.isDigit(c)) {
                sb.append(c);
                signable = false;
                terminatable = true;
                if (type.isEmpty()) {
                    type = "integer";
                    dottable = true;
                }
                underscorable = notLastChar;
                exponentable = !type.equals("exponent");
            } else if ((c == '+' || c == '-') && signable && notLastChar) {
                signable = false;
                terminatable = false;
                if (c == '-') {
                    sb.append('-');
                }
            } else if (c == '.' && dottable && notLastChar) {
                sb.append('.');
                type = "float";
                terminatable = false;
                dottable = false;
                exponentable = false;
                underscorable = false;
            } else if ((c == 'E' || c == 'e') && exponentable && notLastChar) {
                sb.append('E');
                type = "exponent";
                terminatable = false;
                signable = true;
                dottable = false;
                exponentable = false;
                underscorable = false;
            } else if (c == '_' && underscorable && notLastChar && Character.isDigit(s.charAt(i + 1))) {
                underscorable = false;
            } else {
                if (!terminatable) {
                    type = "";
                }
                index.decrementAndGet();
                break;
            }
            i = index.incrementAndGet();
        }
        if (type.equals("integer")) {
            return Long.valueOf(sb.toString());
        }
        if (type.equals("float")) {
            return Double.valueOf(sb.toString());
        }
        if (type.equals("exponent")) {
            String[] exponentString = sb.toString().split("E");
            return Double.parseDouble(exponentString[0]) * Math.pow(10.0, Double.parseDouble(exponentString[1]));
        }
        Results.Errors errors = new Results.Errors();
        errors.invalidValue(context.identifier.getName(), sb.toString(), context.line.get());
        return errors;
    }

    @Override
    public boolean canWrite(Object value) {
        return Number.class.isInstance(value);
    }

    @Override
    public void write(Object value, WriterContext context) {
        context.write(value.toString());
    }

    @Override
    public boolean isPrimitiveType() {
        return true;
    }

    public String toString() {
        return "number";
    }
}

