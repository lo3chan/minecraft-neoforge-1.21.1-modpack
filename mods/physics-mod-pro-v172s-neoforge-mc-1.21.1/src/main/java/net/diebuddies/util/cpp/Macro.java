/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util.cpp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.Token;

public class Macro {
    private Source source;
    private String name;
    private List<String> args;
    private boolean variadic;
    private List<Token> tokens;

    public Macro(Source source, String name) {
        this.source = source;
        this.name = name;
        this.args = null;
        this.variadic = false;
        this.tokens = new ArrayList<Token>();
    }

    public Macro(String name) {
        this(null, name);
    }

    public void setSource(Source s) {
        this.source = s;
    }

    public Source getSource() {
        return this.source;
    }

    public String getName() {
        return this.name;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public boolean isFunctionLike() {
        return this.args != null;
    }

    public int getArgs() {
        return this.args.size();
    }

    public void setVariadic(boolean b) {
        this.variadic = b;
    }

    public boolean isVariadic() {
        return this.variadic;
    }

    public void addToken(Token tok) {
        this.tokens.add(tok);
    }

    public void addPaste(Token tok) {
        this.tokens.add(this.tokens.size() - 1, tok);
    }

    List<Token> getTokens() {
        return this.tokens;
    }

    public String getText() {
        StringBuilder buf = new StringBuilder();
        boolean paste = false;
        for (Token tok : this.tokens) {
            if (tok.getType() == 297) {
                assert (!paste) : "Two sequential pastes.";
                paste = true;
                continue;
            }
            buf.append(tok.getText());
            if (!paste) continue;
            buf.append(" ## ");
            paste = false;
        }
        return buf.toString();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(this.name);
        if (this.args != null) {
            buf.append('(');
            Iterator<String> it = this.args.iterator();
            while (it.hasNext()) {
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append(", ");
                    continue;
                }
                if (!this.isVariadic()) continue;
                buf.append("...");
            }
            buf.append(')');
        }
        if (!this.tokens.isEmpty()) {
            buf.append(" => ").append(this.getText());
        }
        return buf.toString();
    }
}

