/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util.cpp;

import java.io.StringReader;
import net.diebuddies.util.cpp.LexerSource;

public class StringLexerSource
extends LexerSource {
    public StringLexerSource(String string, boolean ppvalid) {
        super(new StringReader(string), ppvalid);
    }

    public StringLexerSource(String string) {
        this(string, false);
    }

    public String toString() {
        return "string literal";
    }
}

