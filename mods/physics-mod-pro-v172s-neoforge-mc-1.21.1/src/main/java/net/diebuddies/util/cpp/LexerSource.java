/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.Feature;
import net.diebuddies.util.cpp.JoinReader;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.NumericValue;
import net.diebuddies.util.cpp.Preprocessor;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.Token;
import net.diebuddies.util.cpp.TokenType;

public class LexerSource
extends Source {
    private static final boolean DEBUG = false;
    private JoinReader reader;
    private final boolean ppvalid;
    private boolean bol;
    private boolean include;
    private boolean digraphs;
    private int u0;
    private int u1;
    private int ucount;
    private int line;
    private int column;
    private int lastcolumn;
    private boolean cr;

    @Nonnull
    protected static BufferedReader toBufferedReader(@Nonnull Reader r) {
        if (r instanceof BufferedReader) {
            return (BufferedReader)r;
        }
        return new BufferedReader(r);
    }

    public LexerSource(Reader r, boolean ppvalid) {
        this.reader = new JoinReader(r);
        this.ppvalid = ppvalid;
        this.bol = true;
        this.include = false;
        this.digraphs = true;
        this.ucount = 0;
        this.line = 1;
        this.column = 0;
        this.lastcolumn = -1;
        this.cr = false;
    }

    @Override
    void init(Preprocessor pp) {
        super.init(pp);
        this.digraphs = pp.getFeature(Feature.DIGRAPHS);
        this.reader.init(pp, this);
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public int getColumn() {
        return this.column;
    }

    @Override
    boolean isNumbered() {
        return true;
    }

    private void _error(String msg, boolean error) throws LexerException {
        int _l = this.line;
        int _c = this.column;
        if (_c == 0) {
            _c = this.lastcolumn;
            --_l;
        } else {
            --_c;
        }
        if (error) {
            super.error(_l, _c, msg);
        } else {
            super.warning(_l, _c, msg);
        }
    }

    final void error(String msg) throws LexerException {
        this._error(msg, true);
    }

    final void warning(String msg) throws LexerException {
        this._error(msg, false);
    }

    void setInclude(boolean b) {
        this.include = b;
    }

    private static boolean isLineSeparator(int c) {
        switch ((char)c) {
            case '\n': 
            case '\u000b': 
            case '\f': 
            case '\r': 
            case '\u0085': 
            case '\u2028': 
            case '\u2029': {
                return true;
            }
        }
        return c == -1;
    }

    private int read() throws IOException, LexerException {
        assert (this.ucount <= 2) : "Illegal ucount: " + this.ucount;
        int c = switch (this.ucount) {
            case 2 -> {
                this.ucount = 1;
                yield this.u1;
            }
            case 1 -> {
                this.ucount = 0;
                yield this.u0;
            }
            default -> this.reader == null ? -1 : this.reader.read();
        };
        switch (c) {
            case 13: {
                this.cr = true;
                ++this.line;
                this.lastcolumn = this.column;
                this.column = 0;
                break;
            }
            case 10: {
                if (this.cr) {
                    this.cr = false;
                    break;
                }
            }
            case 11: 
            case 12: 
            case 133: 
            case 8232: 
            case 8233: {
                this.cr = false;
                ++this.line;
                this.lastcolumn = this.column;
                this.column = 0;
                break;
            }
            case -1: {
                this.cr = false;
                break;
            }
            default: {
                this.cr = false;
                ++this.column;
            }
        }
        return c;
    }

    private void unread(int c) throws IOException {
        if (c != -1) {
            if (LexerSource.isLineSeparator(c)) {
                --this.line;
                this.column = this.lastcolumn;
                this.cr = false;
            } else {
                --this.column;
            }
            switch (this.ucount) {
                case 0: {
                    this.u0 = c;
                    this.ucount = 1;
                    break;
                }
                case 1: {
                    this.u1 = c;
                    this.ucount = 2;
                    break;
                }
                default: {
                    throw new IllegalStateException("Cannot unget another character!");
                }
            }
        }
    }

    @Nonnull
    private Token invalid(StringBuilder text, String reason) throws IOException, LexerException {
        int d = this.read();
        while (!LexerSource.isLineSeparator(d)) {
            text.append((char)d);
            d = this.read();
        }
        this.unread(d);
        return new Token(300, text.toString(), reason);
    }

    @Nonnull
    private Token ccomment() throws IOException, LexerException {
        StringBuilder text = new StringBuilder("/*");
        while (true) {
            int d;
            if ((d = this.read()) == -1) {
                return new Token(300, text.toString(), "Unterminated comment");
            }
            text.append((char)d);
            if (d != 42) continue;
            do {
                if ((d = this.read()) == -1) {
                    return new Token(300, text.toString(), "Unterminated comment");
                }
                text.append((char)d);
            } while (d == 42);
            if (d == 47) break;
        }
        return new Token(260, text.toString());
    }

    @Nonnull
    private Token cppcomment() throws IOException, LexerException {
        StringBuilder text = new StringBuilder("//");
        int d = this.read();
        while (!LexerSource.isLineSeparator(d)) {
            text.append((char)d);
            d = this.read();
        }
        this.unread(d);
        return new Token(261, text.toString());
    }

    private int escape(StringBuilder text) throws IOException, LexerException {
        int d = this.read();
        switch (d) {
            case 97: {
                text.append('a');
                return 7;
            }
            case 98: {
                text.append('b');
                return 8;
            }
            case 102: {
                text.append('f');
                return 12;
            }
            case 110: {
                text.append('n');
                return 10;
            }
            case 114: {
                text.append('r');
                return 13;
            }
            case 116: {
                text.append('t');
                return 9;
            }
            case 118: {
                text.append('v');
                return 11;
            }
            case 92: {
                text.append('\\');
                return 92;
            }
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 53: 
            case 54: 
            case 55: {
                int len = 0;
                int val = 0;
                do {
                    val = (val << 3) + Character.digit(d, 8);
                    text.append((char)d);
                    d = this.read();
                } while (++len < 3 && Character.digit(d, 8) != -1);
                this.unread(d);
                return val;
            }
            case 120: {
                text.append((char)d);
                int len = 0;
                int val = 0;
                while (len++ < 2) {
                    d = this.read();
                    if (Character.digit(d, 16) == -1) {
                        this.unread(d);
                        break;
                    }
                    val = (val << 4) + Character.digit(d, 16);
                    text.append((char)d);
                }
                return val;
            }
            case 34: {
                text.append('\"');
                return 34;
            }
            case 39: {
                text.append('\'');
                return 39;
            }
        }
        this.warning("Unnecessary escape character " + (char)d);
        text.append((char)d);
        return d;
    }

    @Nonnull
    private Token character() throws IOException, LexerException {
        StringBuilder text = new StringBuilder("'");
        int d = this.read();
        if (d == 92) {
            text.append('\\');
            d = this.escape(text);
        } else {
            if (LexerSource.isLineSeparator(d)) {
                this.unread(d);
                return new Token(300, text.toString(), "Unterminated character literal");
            }
            if (d == 39) {
                text.append('\'');
                return new Token(300, text.toString(), "Empty character literal");
            }
            if (!Character.isDefined(d)) {
                text.append('?');
                return this.invalid(text, "Illegal unicode character literal");
            }
            text.append((char)d);
        }
        int e = this.read();
        if (e != 39) {
            while (true) {
                if (LexerSource.isLineSeparator(e)) {
                    this.unread(e);
                    break;
                }
                text.append((char)e);
                if (e == 39) break;
                e = this.read();
            }
            return new Token(300, text.toString(), "Illegal character constant " + String.valueOf(text));
        }
        text.append('\'');
        return new Token(259, text.toString(), Character.valueOf((char)d));
    }

    @Nonnull
    private Token string(char open, char close) throws IOException, LexerException {
        int c;
        StringBuilder text = new StringBuilder();
        text.append(open);
        StringBuilder buf = new StringBuilder();
        while ((c = this.read()) != close) {
            if (c == 92) {
                text.append('\\');
                if (this.include) continue;
                char d = (char)this.escape(text);
                buf.append(d);
                continue;
            }
            if (c == -1) {
                this.unread(c);
                return new Token(300, text.toString(), "End of file in string literal after " + String.valueOf(buf));
            }
            if (LexerSource.isLineSeparator(c)) {
                this.unread(c);
                return new Token(300, text.toString(), "Unterminated string literal after " + String.valueOf(buf));
            }
            text.append((char)c);
            buf.append((char)c);
        }
        text.append(close);
        switch (close) {
            case '\"': {
                return new Token(292, text.toString(), buf.toString());
            }
            case '>': {
                return new Token(269, text.toString(), buf.toString());
            }
            case '\'': {
                if (buf.length() == 1) {
                    return new Token(259, text.toString(), buf.toString());
                }
                return new Token(291, text.toString(), buf.toString());
            }
        }
        throw new IllegalStateException("Unknown closing character " + String.valueOf(close));
    }

    @Nonnull
    private Token _number_suffix(StringBuilder text, NumericValue value, int d) throws IOException, LexerException {
        int flags = 0;
        while (true) {
            if (d == 85 || d == 117) {
                if (flags & true) {
                    this.warning("Duplicate unsigned suffix " + d);
                }
                flags |= 1;
                text.append((char)d);
                d = this.read();
                continue;
            }
            if (d == 76 || d == 108) {
                if ((flags & 0x3E) != 0) {
                    this.warning("Multiple length suffixes after " + String.valueOf(text));
                }
                text.append((char)d);
                int e = this.read();
                if (e == d) {
                    flags |= 8;
                    text.append((char)e);
                    d = this.read();
                    continue;
                }
                flags |= 4;
                d = e;
                continue;
            }
            if (d == 73 || d == 105) {
                if ((flags & 0x3E) != 0) {
                    this.warning("Multiple length suffixes after " + String.valueOf(text));
                }
                flags |= 2;
                text.append((char)d);
                d = this.read();
                continue;
            }
            if (d == 70 || d == 102) {
                if ((flags & 0x3E) != 0) {
                    this.warning("Multiple length suffixes after " + String.valueOf(text));
                }
                flags |= 0x10;
                text.append((char)d);
                d = this.read();
                continue;
            }
            if (d != 68 && d != 100) break;
            if ((flags & 0x3E) != 0) {
                this.warning("Multiple length suffixes after " + String.valueOf(text));
            }
            flags |= 0x20;
            text.append((char)d);
            d = this.read();
        }
        if (Character.isUnicodeIdentifierPart(d)) {
            String reason = "Invalid suffix \"" + (char)d + "\" on numeric constant";
            while (Character.isUnicodeIdentifierPart(d)) {
                text.append((char)d);
                d = this.read();
            }
            this.unread(d);
            return new Token(300, text.toString(), reason);
        }
        this.unread(d);
        value.setFlags(flags);
        return new Token(272, text.toString(), value);
    }

    @Nonnull
    private String _number_part(StringBuilder text, int base, boolean sign) throws IOException, LexerException {
        StringBuilder part = new StringBuilder();
        int d = this.read();
        if (sign && (d == 43 || d == 45)) {
            text.append((char)d);
            part.append((char)d);
            d = this.read();
        }
        while (Character.digit(d, base) != -1) {
            text.append((char)d);
            part.append((char)d);
            d = this.read();
        }
        this.unread(d);
        return part.toString();
    }

    @Nonnull
    private Token number_hex(char x) throws IOException, LexerException {
        StringBuilder text = new StringBuilder("0");
        text.append(x);
        String integer = this._number_part(text, 16, false);
        NumericValue value = new NumericValue(16, integer);
        int d = this.read();
        if (d == 46) {
            text.append((char)d);
            String fraction = this._number_part(text, 16, false);
            value.setFractionalPart(fraction);
            d = this.read();
        }
        if (d == 80 || d == 112) {
            text.append((char)d);
            String exponent = this._number_part(text, 10, true);
            value.setExponent(2, exponent);
            d = this.read();
        }
        return this._number_suffix(text, value, d);
    }

    private static boolean is_octal(@Nonnull String text) {
        if (!text.startsWith("0")) {
            return false;
        }
        for (int i = 0; i < text.length(); ++i) {
            if (Character.digit(text.charAt(i), 8) != -1) continue;
            return false;
        }
        return true;
    }

    @Nonnull
    private Token number_decimal() throws IOException, LexerException {
        StringBuilder text = new StringBuilder();
        String integer = this._number_part(text, 10, false);
        String fraction = null;
        String exponent = null;
        int d = this.read();
        if (d == 46) {
            text.append((char)d);
            fraction = this._number_part(text, 10, false);
            d = this.read();
        }
        if (d == 69 || d == 101) {
            text.append((char)d);
            exponent = this._number_part(text, 10, true);
            d = this.read();
        }
        int base = 10;
        if (fraction == null && exponent == null && integer.startsWith("0")) {
            if (!LexerSource.is_octal(integer)) {
                this.warning("Decimal constant starts with 0, but not octal: " + integer);
            } else {
                base = 8;
            }
        }
        NumericValue value = new NumericValue(base, integer);
        if (fraction != null) {
            value.setFractionalPart(fraction);
        }
        if (exponent != null) {
            value.setExponent(10, exponent);
        }
        return this._number_suffix(text, value, d);
    }

    @Nonnull
    private Token number() throws IOException, LexerException {
        Token tok;
        int c = this.read();
        if (c == 48) {
            int d = this.read();
            if (d == 120 || d == 88) {
                tok = this.number_hex((char)d);
            } else {
                this.unread(d);
                this.unread(c);
                tok = this.number_decimal();
            }
        } else if (Character.isDigit(c) || c == 46) {
            this.unread(c);
            tok = this.number_decimal();
        } else {
            throw new LexerException("Asked to parse something as a number which isn't: " + (char)c);
        }
        return tok;
    }

    @Nonnull
    private Token identifier(int c) throws IOException, LexerException {
        int d;
        StringBuilder text = new StringBuilder();
        text.append((char)c);
        while (true) {
            if (Character.isIdentifierIgnorable(d = this.read())) {
                continue;
            }
            if (!Character.isJavaIdentifierPart(d)) break;
            text.append((char)d);
        }
        this.unread(d);
        return new Token(270, text.toString());
    }

    @Nonnull
    private Token whitespace(int c) throws IOException, LexerException {
        int d;
        StringBuilder text = new StringBuilder();
        text.append((char)c);
        while (true) {
            d = this.read();
            if (this.ppvalid && LexerSource.isLineSeparator(d) || !Character.isWhitespace(d)) break;
            text.append((char)d);
        }
        this.unread(d);
        return new Token(294, text.toString());
    }

    @Nonnull
    private Token cond(char c, int yes, int no) throws IOException, LexerException {
        int d = this.read();
        if (c == d) {
            return new Token(yes);
        }
        this.unread(d);
        return new Token(no);
    }

    @Override
    public Token token() throws IOException, LexerException {
        Token tok = null;
        int _l = this.line;
        int _c = this.column;
        int c = this.read();
        switch (c) {
            case 10: {
                if (!this.ppvalid) break;
                this.bol = true;
                if (this.include) {
                    tok = new Token(284, _l, _c, "\n");
                } else {
                    int d;
                    int nls = 0;
                    do {
                        ++nls;
                    } while ((d = this.read()) == 10);
                    this.unread(d);
                    char[] text = new char[nls];
                    for (int i = 0; i < text.length; ++i) {
                        text[i] = 10;
                    }
                    tok = new Token(284, _l, _c, new String(text));
                }
                return tok;
            }
            case 33: {
                tok = this.cond('=', 283, 33);
                break;
            }
            case 35: {
                if (this.bol) {
                    tok = new Token(268);
                    break;
                }
                tok = this.cond('#', 286, 35);
                break;
            }
            case 43: {
                int d = this.read();
                if (d == 43) {
                    tok = new Token(271);
                    break;
                }
                if (d == 61) {
                    tok = new Token(287);
                    break;
                }
                this.unread(d);
                break;
            }
            case 45: {
                int d = this.read();
                if (d == 45) {
                    tok = new Token(262);
                    break;
                }
                if (d == 61) {
                    tok = new Token(293);
                    break;
                }
                if (d == 62) {
                    tok = new Token(258);
                    break;
                }
                this.unread(d);
                break;
            }
            case 42: {
                tok = this.cond('=', 282, 42);
                break;
            }
            case 47: {
                int d = this.read();
                if (d == 42) {
                    tok = this.ccomment();
                    break;
                }
                if (d == 47) {
                    tok = this.cppcomment();
                    break;
                }
                if (d == 61) {
                    tok = new Token(263);
                    break;
                }
                this.unread(d);
                break;
            }
            case 37: {
                int d = this.read();
                if (d == 61) {
                    tok = new Token(281);
                    break;
                }
                if (this.digraphs && d == 62) {
                    tok = new Token(125);
                    break;
                }
                if (this.digraphs && d == 58) {
                    d = this.read();
                    if (d != 37) {
                        this.unread(d);
                        tok = new Token(35);
                        break;
                    }
                    d = this.read();
                    if (d != 58) {
                        this.unread(d);
                        this.unread(37);
                        tok = new Token(35);
                        break;
                    }
                    tok = new Token(286);
                    break;
                }
                this.unread(d);
                break;
            }
            case 58: {
                int d = this.read();
                if (this.digraphs && d == 62) {
                    tok = new Token(93);
                    break;
                }
                this.unread(d);
                break;
            }
            case 60: {
                if (this.include) {
                    tok = this.string('<', '>');
                    break;
                }
                int d = this.read();
                if (d == 61) {
                    tok = new Token(275);
                    break;
                }
                if (d == 60) {
                    tok = this.cond('=', 280, 279);
                    break;
                }
                if (this.digraphs && d == 58) {
                    tok = new Token(91);
                    break;
                }
                if (this.digraphs && d == 37) {
                    tok = new Token(123);
                    break;
                }
                this.unread(d);
                break;
            }
            case 61: {
                tok = this.cond('=', 266, 61);
                break;
            }
            case 62: {
                int d = this.read();
                if (d == 61) {
                    tok = new Token(267);
                    break;
                }
                if (d == 62) {
                    tok = this.cond('=', 290, 289);
                    break;
                }
                this.unread(d);
                break;
            }
            case 94: {
                tok = this.cond('=', 295, 94);
                break;
            }
            case 124: {
                int d = this.read();
                if (d == 61) {
                    tok = new Token(285);
                    break;
                }
                if (d == 124) {
                    tok = this.cond('=', 278, 277);
                    break;
                }
                this.unread(d);
                break;
            }
            case 38: {
                int d = this.read();
                if (d == 38) {
                    tok = this.cond('=', 274, 273);
                    break;
                }
                if (d == 61) {
                    tok = new Token(257);
                    break;
                }
                this.unread(d);
                break;
            }
            case 46: {
                int d = this.read();
                if (d == 46) {
                    tok = this.cond('.', 264, 288);
                } else {
                    this.unread(d);
                }
                if (!Character.isDigit(d)) break;
                this.unread(46);
                tok = this.number();
                break;
            }
            case 39: {
                tok = this.string('\'', '\'');
                break;
            }
            case 34: {
                tok = this.string('\"', '\"');
                break;
            }
            case -1: {
                this.close();
                tok = new Token(265, _l, _c, "<eof>");
            }
        }
        if (tok == null) {
            if (Character.isWhitespace(c)) {
                tok = this.whitespace(c);
            } else if (Character.isDigit(c)) {
                this.unread(c);
                tok = this.number();
            } else if (Character.isJavaIdentifierStart(c)) {
                tok = this.identifier(c);
            } else {
                String text = TokenType.getTokenText(c);
                if (text == null) {
                    text = c >>> 16 == 0 ? Character.toString((char)c) : new String(Character.toChars(c));
                }
                tok = new Token(c, text);
            }
        }
        if (this.bol) {
            switch (tok.getType()) {
                case 260: 
                case 294: {
                    break;
                }
                default: {
                    this.bol = false;
                }
            }
        }
        tok.setLocation(_l, _c);
        return tok;
    }

    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
        super.close();
    }
}

