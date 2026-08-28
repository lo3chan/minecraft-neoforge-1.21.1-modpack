/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.Token;

class FixedTokenSource
extends Source {
    private static final Token EOF = new Token(265, "<ts-eof>");
    private final List<Token> tokens;
    private int idx;

    FixedTokenSource(Token ... tokens) {
        this.tokens = Arrays.asList(tokens);
        this.idx = 0;
    }

    FixedTokenSource(List<Token> tokens) {
        this.tokens = tokens;
        this.idx = 0;
    }

    @Override
    public Token token() throws IOException, LexerException {
        if (this.idx >= this.tokens.size()) {
            return EOF;
        }
        return this.tokens.get(this.idx++);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("constant token stream ").append(this.tokens);
        Source parent = this.getParent();
        if (parent != null) {
            buf.append(" in ").append(String.valueOf(parent));
        }
        return buf.toString();
    }
}

