/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.Token;

public class SourceIterator
implements Iterator<Token> {
    private final Source source;
    private Token tok;

    public SourceIterator(@Nonnull Source s) {
        this.source = s;
        this.tok = null;
    }

    private void advance() {
        try {
            if (this.tok == null) {
                this.tok = this.source.token();
            }
        }
        catch (LexerException e) {
            throw new IllegalStateException(e);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean hasNext() {
        this.advance();
        return this.tok.getType() != 265;
    }

    @Override
    public Token next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        Token t = this.tok;
        this.tok = null;
        return t;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

