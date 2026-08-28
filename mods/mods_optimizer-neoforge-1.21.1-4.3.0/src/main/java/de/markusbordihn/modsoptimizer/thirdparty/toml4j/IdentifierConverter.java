/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Context;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Identifier;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Keys;
import java.util.concurrent.atomic.AtomicInteger;

class IdentifierConverter {
    static final IdentifierConverter IDENTIFIER_CONVERTER = new IdentifierConverter();

    Identifier convert(String s, AtomicInteger index, Context context) {
        boolean quoted = false;
        StringBuilder name = new StringBuilder();
        boolean terminated = false;
        boolean isKey = s.charAt(index.get()) != '[';
        boolean isTableArray = !isKey && s.length() > index.get() + 1 && s.charAt(index.get() + 1) == '[';
        boolean inComment = false;
        int i = index.get();
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Keys.isQuote(c) && (i == 0 || s.charAt(i - 1) != '\\')) {
                quoted = !quoted;
                name.append(c);
            } else {
                if (c == '\n') {
                    index.decrementAndGet();
                    break;
                }
                if (quoted) {
                    name.append(c);
                } else {
                    if (c == '=' && isKey) {
                        terminated = true;
                        break;
                    }
                    if (c == ']' && !isKey) {
                        if (!isTableArray || s.length() > index.get() + 1 && s.charAt(index.get() + 1) == ']') {
                            terminated = true;
                            name.append(']');
                            if (isTableArray) {
                                name.append(']');
                            }
                        }
                    } else if (terminated && c == '#') {
                        inComment = true;
                    } else {
                        if (terminated && !Character.isWhitespace(c) && !inComment) {
                            terminated = false;
                            break;
                        }
                        if (!terminated) {
                            name.append(c);
                        }
                    }
                }
            }
            i = index.incrementAndGet();
        }
        if (!terminated) {
            if (isKey) {
                context.errors.unterminatedKey(name.toString(), context.line.get());
            } else {
                context.errors.invalidKey(name.toString(), context.line.get());
            }
            return Identifier.INVALID;
        }
        return Identifier.from(name.toString(), context);
    }

    private IdentifierConverter() {
    }
}

