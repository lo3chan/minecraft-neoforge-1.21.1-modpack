/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import net.diebuddies.util.cpp.Feature;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.LexerSource;
import net.diebuddies.util.cpp.Preprocessor;
import net.diebuddies.util.cpp.PreprocessorListener;
import net.diebuddies.util.cpp.Warning;

class JoinReader
implements Closeable {
    private final Reader in;
    private PreprocessorListener listener;
    private LexerSource source;
    private boolean trigraphs;
    private boolean warnings;
    private int newlines;
    private boolean flushnl;
    private int[] unget;
    private int uptr;

    public JoinReader(Reader in, boolean trigraphs) {
        this.in = in;
        this.trigraphs = trigraphs;
        this.newlines = 0;
        this.flushnl = false;
        this.unget = new int[2];
        this.uptr = 0;
    }

    public JoinReader(Reader in) {
        this(in, false);
    }

    public void setTrigraphs(boolean enable, boolean warnings) {
        this.trigraphs = enable;
        this.warnings = warnings;
    }

    void init(Preprocessor pp, LexerSource s) {
        this.listener = pp.getListener();
        this.source = s;
        this.setTrigraphs(pp.getFeature(Feature.TRIGRAPHS), pp.getWarning(Warning.TRIGRAPHS));
    }

    private int __read() throws IOException {
        if (this.uptr > 0) {
            return this.unget[--this.uptr];
        }
        return this.in.read();
    }

    private void _unread(int c) {
        if (c != -1) {
            this.unget[this.uptr++] = c;
        }
        assert (this.uptr <= this.unget.length) : "JoinReader ungets too many characters";
    }

    protected void warning(String msg) throws LexerException {
        if (this.source == null) {
            throw new LexerException(msg);
        }
        this.source.warning(msg);
    }

    private char trigraph(char raw, char repl) throws IOException, LexerException {
        if (this.trigraphs) {
            if (this.warnings) {
                this.warning("trigraph ??" + raw + " converted to " + repl);
            }
            return repl;
        }
        if (this.warnings) {
            this.warning("trigraph ??" + raw + " ignored");
        }
        this._unread(raw);
        this._unread(63);
        return '?';
    }

    private int _read() throws IOException, LexerException {
        int c = this.__read();
        if (c == 63 && (this.trigraphs || this.warnings)) {
            int d = this.__read();
            if (d == 63) {
                int e = this.__read();
                switch (e) {
                    case 40: {
                        return this.trigraph('(', '[');
                    }
                    case 41: {
                        return this.trigraph(')', ']');
                    }
                    case 60: {
                        return this.trigraph('<', '{');
                    }
                    case 62: {
                        return this.trigraph('>', '}');
                    }
                    case 61: {
                        return this.trigraph('=', '#');
                    }
                    case 47: {
                        return this.trigraph('/', '\\');
                    }
                    case 39: {
                        return this.trigraph('\'', '^');
                    }
                    case 33: {
                        return this.trigraph('!', '|');
                    }
                    case 45: {
                        return this.trigraph('-', '~');
                    }
                }
                this._unread(e);
            }
            this._unread(d);
        }
        return c;
    }

    public int read() throws IOException, LexerException {
        int c;
        if (this.flushnl) {
            if (this.newlines > 0) {
                --this.newlines;
                return 10;
            }
            this.flushnl = false;
        }
        block9: while (true) {
            c = this._read();
            switch (c) {
                case 92: {
                    int d = this._read();
                    switch (d) {
                        case 10: {
                            ++this.newlines;
                            continue block9;
                        }
                        case 13: {
                            ++this.newlines;
                            int e = this._read();
                            if (e == 10) continue block9;
                            this._unread(e);
                            continue block9;
                        }
                    }
                    this._unread(d);
                    return c;
                }
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 133: 
                case 8232: 
                case 8233: {
                    this.flushnl = true;
                    return c;
                }
                case -1: {
                    if (this.newlines <= 0) break block9;
                    --this.newlines;
                    return 10;
                }
            }
            break;
        }
        return c;
    }

    public int read(char[] cbuf, int off, int len) throws IOException, LexerException {
        for (int i = 0; i < len; ++i) {
            int ch = this.read();
            if (ch == -1) {
                return i;
            }
            cbuf[off + i] = (char)ch;
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    public String toString() {
        return "JoinReader(nl=" + this.newlines + ")";
    }
}

