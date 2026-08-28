/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Preprocessor;
import net.diebuddies.util.cpp.Token;

class Argument
extends ArrayList<Token> {
    private List<Token> expansion = null;

    public void addToken(@Nonnull Token tok) {
        this.add(tok);
    }

    void expand(@Nonnull Preprocessor p) throws IOException, LexerException {
        if (this.expansion == null) {
            this.expansion = p.expand(this);
        }
    }

    @Nonnull
    public Iterator<Token> expansion() {
        return this.expansion.iterator();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Argument(");
        buf.append("raw=[ ");
        for (int i = 0; i < this.size(); ++i) {
            buf.append(((Token)this.get(i)).getText());
        }
        buf.append(" ];expansion=[ ");
        if (this.expansion == null) {
            buf.append("null");
        } else {
            for (Token token : this.expansion) {
                buf.append(token.getText());
            }
        }
        buf.append(" ])");
        return buf.toString();
    }
}

