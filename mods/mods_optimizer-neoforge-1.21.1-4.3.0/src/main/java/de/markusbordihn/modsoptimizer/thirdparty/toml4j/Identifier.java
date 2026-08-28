/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Keys;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.StringValueReaderWriter;

class Identifier {
    static final Identifier INVALID = new Identifier("", null);
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-";
    private final String name;
    private final Type type;

    static Identifier from(String name, Context context) {
        boolean valid;
        Type type;
        if ((name = name.trim()).startsWith("[[")) {
            type = Type.TABLE_ARRAY;
            valid = Identifier.isValidTableArray(name, context);
        } else if (name.startsWith("[")) {
            type = Type.TABLE;
            valid = Identifier.isValidTable(name, context);
        } else {
            type = Type.KEY;
            valid = Identifier.isValidKey(name, context);
        }
        if (!valid) {
            return INVALID;
        }
        return new Identifier(Identifier.extractName(name), type);
    }

    private Identifier(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    String getName() {
        return this.name;
    }

    String getBareName() {
        if (this.isKey()) {
            return this.name;
        }
        if (this.isTable()) {
            return this.name.substring(1, this.name.length() - 1);
        }
        return this.name.substring(2, this.name.length() - 2);
    }

    boolean isKey() {
        return this.type == Type.KEY;
    }

    boolean isTable() {
        return this.type == Type.TABLE;
    }

    boolean isTableArray() {
        return this.type == Type.TABLE_ARRAY;
    }

    private static String extractName(String raw) {
        boolean quoted = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c == '\"' && (i == 0 || raw.charAt(i - 1) != '\\')) {
                quoted = !quoted;
                sb.append('\"');
                continue;
            }
            if (!quoted && Character.isWhitespace(c)) continue;
            sb.append(c);
        }
        return StringValueReaderWriter.STRING_VALUE_READER_WRITER.replaceUnicodeCharacters(sb.toString());
    }

    private static boolean isValidKey(String name, Context context) {
        if (name.trim().isEmpty()) {
            context.errors.invalidKey(name, context.line.get());
            return false;
        }
        boolean quoted = false;
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c == '\"' && (i == 0 || name.charAt(i - 1) != '\\')) {
                if (!quoted && i > 0 && name.charAt(i - 1) != '.') {
                    context.errors.invalidKey(name, context.line.get());
                    return false;
                }
                quoted = !quoted;
                continue;
            }
            if (quoted || ALLOWED_CHARS.indexOf(c) != -1) continue;
            context.errors.invalidKey(name, context.line.get());
            return false;
        }
        return true;
    }

    private static boolean isValidTable(String name, Context context) {
        String trimmed;
        boolean valid = true;
        if (!name.endsWith("]")) {
            valid = false;
        }
        if ((trimmed = name.substring(1, name.length() - 1).trim()).isEmpty() || trimmed.charAt(0) == '.' || trimmed.endsWith(".")) {
            valid = false;
        }
        if (!valid) {
            context.errors.invalidTable(name, context.line.get());
            return false;
        }
        boolean quoted = false;
        boolean dotAllowed = false;
        boolean quoteAllowed = true;
        boolean charAllowed = true;
        for (int i = 0; i < trimmed.length(); ++i) {
            char c = trimmed.charAt(i);
            if (!valid) break;
            if (Keys.isQuote(c)) {
                if (!quoteAllowed) {
                    valid = false;
                    continue;
                }
                if (quoted && trimmed.charAt(i - 1) != '\\') {
                    charAllowed = false;
                    dotAllowed = true;
                    quoteAllowed = false;
                    continue;
                }
                if (quoted) continue;
                quoted = true;
                quoteAllowed = true;
                continue;
            }
            if (quoted) continue;
            if (c == '.') {
                if (dotAllowed) {
                    charAllowed = true;
                    dotAllowed = false;
                    quoteAllowed = true;
                    continue;
                }
                context.errors.emptyImplicitTable(name, context.line.get());
                return false;
            }
            if (Character.isWhitespace(c)) {
                char prev = trimmed.charAt(i - 1);
                if (Character.isWhitespace(prev) || prev == '.') continue;
                charAllowed = false;
                dotAllowed = true;
                quoteAllowed = true;
                continue;
            }
            if (charAllowed && ALLOWED_CHARS.indexOf(c) > -1) {
                charAllowed = true;
                dotAllowed = true;
                quoteAllowed = false;
                continue;
            }
            valid = false;
        }
        if (!valid) {
            context.errors.invalidTable(name, context.line.get());
            return false;
        }
        return true;
    }

    private static boolean isValidTableArray(String line, Context context) {
        String trimmed;
        boolean valid = true;
        if (!line.endsWith("]]")) {
            valid = false;
        }
        if ((trimmed = line.substring(2, line.length() - 2).trim()).isEmpty() || trimmed.charAt(0) == '.' || trimmed.endsWith(".")) {
            valid = false;
        }
        if (!valid) {
            context.errors.invalidTableArray(line, context.line.get());
            return false;
        }
        boolean quoted = false;
        boolean dotAllowed = false;
        boolean quoteAllowed = true;
        boolean charAllowed = true;
        for (int i = 0; i < trimmed.length(); ++i) {
            char c = trimmed.charAt(i);
            if (!valid) break;
            if (c == '\"') {
                if (!quoteAllowed) {
                    valid = false;
                    continue;
                }
                if (quoted && trimmed.charAt(i - 1) != '\\') {
                    charAllowed = false;
                    dotAllowed = true;
                    quoteAllowed = false;
                    continue;
                }
                if (quoted) continue;
                quoted = true;
                quoteAllowed = true;
                continue;
            }
            if (quoted) continue;
            if (c == '.') {
                if (dotAllowed) {
                    charAllowed = true;
                    dotAllowed = false;
                    quoteAllowed = true;
                    continue;
                }
                context.errors.emptyImplicitTable(line, context.line.get());
                return false;
            }
            if (Character.isWhitespace(c)) {
                char prev = trimmed.charAt(i - 1);
                if (Character.isWhitespace(prev) || prev == '.' || prev == '\"') continue;
                charAllowed = false;
                dotAllowed = true;
                quoteAllowed = true;
                continue;
            }
            if (charAllowed && ALLOWED_CHARS.indexOf(c) > -1) {
                charAllowed = true;
                dotAllowed = true;
                quoteAllowed = false;
                continue;
            }
            valid = false;
        }
        if (!valid) {
            context.errors.invalidTableArray(line, context.line.get());
            return false;
        }
        return true;
    }

    private static enum Type {
        KEY,
        TABLE,
        TABLE_ARRAY;

    }
}

