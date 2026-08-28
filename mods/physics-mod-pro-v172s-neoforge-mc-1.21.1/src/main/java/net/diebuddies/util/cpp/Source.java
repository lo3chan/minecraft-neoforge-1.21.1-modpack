/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnegative
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Macro;
import net.diebuddies.util.cpp.Preprocessor;
import net.diebuddies.util.cpp.PreprocessorListener;
import net.diebuddies.util.cpp.SourceIterator;
import net.diebuddies.util.cpp.Token;
import net.diebuddies.util.cpp.Warning;

public abstract class Source
implements Iterable<Token>,
Closeable {
    private Source parent = null;
    private boolean autopop = false;
    private PreprocessorListener listener = null;
    private boolean active = true;
    private boolean werror = false;

    void setParent(Source parent, boolean autopop) {
        this.parent = parent;
        this.autopop = autopop;
    }

    final Source getParent() {
        return this.parent;
    }

    void init(Preprocessor pp) {
        this.setListener(pp.getListener());
        this.werror = pp.getWarnings().contains((Object)Warning.ERROR);
    }

    public void setListener(PreprocessorListener pl) {
        this.listener = pl;
    }

    @CheckForNull
    public String getPath() {
        Source parent = this.getParent();
        if (parent != null) {
            return parent.getPath();
        }
        return null;
    }

    @CheckForNull
    public String getName() {
        Source parent = this.getParent();
        if (parent != null) {
            return parent.getName();
        }
        return null;
    }

    @Nonnegative
    public int getLine() {
        Source parent = this.getParent();
        if (parent == null) {
            return 0;
        }
        return parent.getLine();
    }

    public int getColumn() {
        Source parent = this.getParent();
        if (parent == null) {
            return 0;
        }
        return parent.getColumn();
    }

    boolean isExpanding(@Nonnull Macro m) {
        Source parent = this.getParent();
        if (parent != null) {
            return parent.isExpanding(m);
        }
        return false;
    }

    boolean isAutopop() {
        return this.autopop;
    }

    boolean isNumbered() {
        return false;
    }

    void setActive(boolean b) {
        this.active = b;
    }

    boolean isActive() {
        return this.active;
    }

    @Nonnull
    public abstract Token token() throws IOException, LexerException;

    @Override
    public Iterator<Token> iterator() {
        return new SourceIterator(this);
    }

    @Nonnull
    public Token skipline(boolean white) throws IOException, LexerException {
        block5: while (true) {
            Token tok = this.token();
            switch (tok.getType()) {
                case 265: {
                    this.warning(tok.getLine(), tok.getColumn(), "No newline before end of file");
                    return new Token(284, tok.getLine(), tok.getColumn(), "\n");
                }
                case 284: {
                    return tok;
                }
                case 260: 
                case 261: 
                case 294: {
                    continue block5;
                }
            }
            if (!white) continue;
            this.warning(tok.getLine(), tok.getColumn(), "Unexpected nonwhite token");
        }
    }

    protected void error(int line, int column, String msg) throws LexerException {
        if (this.listener == null) {
            throw new LexerException("Error at " + line + ":" + column + ": " + msg);
        }
        this.listener.handleError(this, line, column, msg);
    }

    protected void warning(int line, int column, String msg) throws LexerException {
        if (this.werror) {
            this.error(line, column, msg);
        } else if (this.listener != null) {
            this.listener.handleWarning(this, line, column, msg);
        } else {
            throw new LexerException("Warning at " + line + ":" + column + ": " + msg);
        }
    }

    @Override
    public void close() throws IOException {
    }
}

