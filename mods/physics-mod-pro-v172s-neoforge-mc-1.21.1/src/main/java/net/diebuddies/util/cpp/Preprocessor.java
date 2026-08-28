/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.Argument;
import net.diebuddies.util.cpp.Feature;
import net.diebuddies.util.cpp.FileLexerSource;
import net.diebuddies.util.cpp.FixedTokenSource;
import net.diebuddies.util.cpp.InternalException;
import net.diebuddies.util.cpp.JavaFileSystem;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.LexerSource;
import net.diebuddies.util.cpp.Macro;
import net.diebuddies.util.cpp.MacroTokenSource;
import net.diebuddies.util.cpp.NumericValue;
import net.diebuddies.util.cpp.PreprocessorCommand;
import net.diebuddies.util.cpp.PreprocessorListener;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.State;
import net.diebuddies.util.cpp.StringLexerSource;
import net.diebuddies.util.cpp.Token;
import net.diebuddies.util.cpp.VirtualFile;
import net.diebuddies.util.cpp.VirtualFileSystem;
import net.diebuddies.util.cpp.Warning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Preprocessor
implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(Preprocessor.class);
    private static final Source INTERNAL = new Source(){

        @Override
        public Token token() throws IOException, LexerException {
            throw new LexerException("Cannot read from " + this.getName());
        }

        @Override
        public String getPath() {
            return "<internal-data>";
        }

        @Override
        public String getName() {
            return "internal data";
        }
    };
    private static final Macro __LINE__ = new Macro(INTERNAL, "__LINE__");
    private static final Macro __FILE__ = new Macro(INTERNAL, "__FILE__");
    private static final Macro __COUNTER__ = new Macro(INTERNAL, "__COUNTER__");
    private final List<Source> inputs;
    private final Map<String, Macro> macros;
    private final Stack<State> states;
    private Source source;
    private int counter;
    private final Set<String> onceseenpaths = new HashSet<String>();
    private final List<VirtualFile> includes = new ArrayList<VirtualFile>();
    private List<String> quoteincludepath;
    private List<String> sysincludepath;
    private List<String> frameworkspath;
    private final Set<Feature> features;
    private final Set<Warning> warnings;
    private VirtualFileSystem filesystem;
    private PreprocessorListener listener;
    private Token source_token;
    @CheckForNull
    private Token expr_token = null;

    public Preprocessor() {
        this.inputs = new ArrayList<Source>();
        this.macros = new HashMap<String, Macro>();
        this.macros.put(__LINE__.getName(), __LINE__);
        this.macros.put(__FILE__.getName(), __FILE__);
        this.macros.put(__COUNTER__.getName(), __COUNTER__);
        this.states = new Stack();
        this.states.push(new State());
        this.source = null;
        this.counter = 0;
        this.quoteincludepath = new ArrayList<String>();
        this.sysincludepath = new ArrayList<String>();
        this.frameworkspath = new ArrayList<String>();
        this.features = EnumSet.noneOf(Feature.class);
        this.warnings = EnumSet.noneOf(Warning.class);
        this.filesystem = new JavaFileSystem();
        this.listener = null;
    }

    public Preprocessor(@Nonnull Source initial) {
        this();
        this.addInput(initial);
    }

    public Preprocessor(@Nonnull File file) throws IOException {
        this(new FileLexerSource(file));
    }

    public void setFileSystem(@Nonnull VirtualFileSystem filesystem) {
        this.filesystem = filesystem;
    }

    @Nonnull
    public VirtualFileSystem getFileSystem() {
        return this.filesystem;
    }

    public void setListener(@Nonnull PreprocessorListener listener) {
        this.listener = listener;
        for (Source s = this.source; s != null; s = s.getParent()) {
            s.init(this);
        }
    }

    @Nonnull
    public PreprocessorListener getListener() {
        return this.listener;
    }

    @Nonnull
    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void addFeature(@Nonnull Feature f) {
        this.features.add(f);
    }

    public void addFeatures(@Nonnull Collection<Feature> f) {
        this.features.addAll(f);
    }

    public void addFeatures(Feature ... f) {
        this.addFeatures(Arrays.asList(f));
    }

    public boolean getFeature(@Nonnull Feature f) {
        return this.features.contains((Object)f);
    }

    @Nonnull
    public Set<Warning> getWarnings() {
        return this.warnings;
    }

    public void addWarning(@Nonnull Warning w) {
        this.warnings.add(w);
    }

    public void addWarnings(@Nonnull Collection<Warning> w) {
        this.warnings.addAll(w);
    }

    public boolean getWarning(@Nonnull Warning w) {
        return this.warnings.contains((Object)w);
    }

    public void addInput(@Nonnull Source source) {
        source.init(this);
        this.inputs.add(source);
    }

    public void addInput(@Nonnull File file) throws IOException {
        this.addInput(new FileLexerSource(file));
    }

    protected void error(int line, int column, @Nonnull String msg) throws LexerException {
        if (this.listener == null) {
            throw new LexerException("Error at " + line + ":" + column + ": " + msg);
        }
        this.listener.handleError(this.source, line, column, msg);
    }

    protected void error(@Nonnull Token tok, @Nonnull String msg) throws LexerException {
        this.error(tok.getLine(), tok.getColumn(), msg);
    }

    protected void warning(int line, int column, @Nonnull String msg) throws LexerException {
        if (this.warnings.contains((Object)Warning.ERROR)) {
            this.error(line, column, msg);
        } else if (this.listener != null) {
            this.listener.handleWarning(this.source, line, column, msg);
        } else {
            throw new LexerException("Warning at " + line + ":" + column + ": " + msg);
        }
    }

    protected void warning(@Nonnull Token tok, @Nonnull String msg) throws LexerException {
        this.warning(tok.getLine(), tok.getColumn(), msg);
    }

    public void addMacro(@Nonnull Macro m) throws LexerException {
        String name = m.getName();
        if ("defined".equals(name)) {
            throw new LexerException("Cannot redefine name 'defined'");
        }
        this.macros.put(m.getName(), m);
    }

    public void addMacro(@Nonnull String name, @Nonnull String value) throws LexerException {
        try {
            Token tok;
            Macro m = new Macro(name);
            StringLexerSource s = new StringLexerSource(value);
            while ((tok = s.token()).getType() != 265) {
                m.addToken(tok);
            }
            this.addMacro(m);
        }
        catch (IOException e) {
            throw new LexerException(e);
        }
    }

    public void addMacro(@Nonnull String name) throws LexerException {
        this.addMacro(name, "1");
    }

    public void setQuoteIncludePath(@Nonnull List<String> path) {
        this.quoteincludepath = path;
    }

    @Nonnull
    public List<String> getQuoteIncludePath() {
        return this.quoteincludepath;
    }

    public void setSystemIncludePath(@Nonnull List<String> path) {
        this.sysincludepath = path;
    }

    @Nonnull
    public List<String> getSystemIncludePath() {
        return this.sysincludepath;
    }

    public void setFrameworksPath(@Nonnull List<String> path) {
        this.frameworkspath = path;
    }

    @Nonnull
    public List<String> getFrameworksPath() {
        return this.frameworkspath;
    }

    @Nonnull
    public Map<String, Macro> getMacros() {
        return this.macros;
    }

    @CheckForNull
    public Macro getMacro(@Nonnull String name) {
        return this.macros.get(name);
    }

    @Nonnull
    public List<? extends VirtualFile> getIncludes() {
        return this.includes;
    }

    private void push_state() {
        State top = this.states.peek();
        this.states.push(new State(top));
    }

    private void pop_state() throws LexerException {
        State s = this.states.pop();
        if (this.states.isEmpty()) {
            this.error(0, 0, "#endif without #if");
            this.states.push(s);
        }
    }

    private boolean isActive() {
        State state = this.states.peek();
        return state.isParentActive() && state.isActive();
    }

    protected Source getSource() {
        return this.source;
    }

    protected void push_source(@Nonnull Source source, boolean autopop) {
        source.init(this);
        source.setParent(this.source, autopop);
        if (this.listener != null) {
            this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.SUSPEND);
        }
        this.source = source;
        if (this.listener != null) {
            this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.PUSH);
        }
    }

    @CheckForNull
    protected Token pop_source(boolean linemarker) throws IOException {
        if (this.listener != null) {
            this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.POP);
        }
        Source s = this.source;
        this.source = s.getParent();
        s.close();
        if (this.listener != null && this.source != null) {
            this.listener.handleSourceChange(this.source, PreprocessorListener.SourceChangeEvent.RESUME);
        }
        Source t = this.getSource();
        if (this.getFeature(Feature.LINEMARKERS) && s.isNumbered() && t != null) {
            return this.line_token(t.getLine(), t.getName(), " 2");
        }
        return null;
    }

    protected void pop_source() throws IOException {
        this.pop_source(false);
    }

    @Nonnull
    private Token next_source() {
        if (this.inputs.isEmpty()) {
            return new Token(265);
        }
        Source s = this.inputs.remove(0);
        this.push_source(s, true);
        return this.line_token(s.getLine(), s.getName(), " 1");
    }

    @Nonnull
    private Token line_token(int line, @CheckForNull String name, @Nonnull String extra) {
        StringBuilder buf = new StringBuilder();
        buf.append("#line ").append(line).append(" \"");
        if (name == null) {
            buf.append("<no file>");
        } else {
            MacroTokenSource.escape(buf, name);
        }
        buf.append("\"").append(extra).append("\n");
        return new Token(299, line, 0, buf.toString(), null);
    }

    @Nonnull
    private Token source_token() throws IOException, LexerException {
        Token tok;
        block5: {
            Token mark;
            if (this.source_token != null) {
                Token tok2 = this.source_token;
                this.source_token = null;
                if (this.getFeature(Feature.DEBUG)) {
                    LOG.debug("Returning unget token " + String.valueOf(tok2));
                }
                return tok2;
            }
            while (true) {
                Source s;
                if ((s = this.getSource()) == null) {
                    Token t = this.next_source();
                    if (t.getType() == 299 && !this.getFeature(Feature.LINEMARKERS)) continue;
                    return t;
                }
                tok = s.token();
                if (tok.getType() != 265 || !s.isAutopop()) break block5;
                mark = this.pop_source(true);
                if (mark != null) break;
            }
            return mark;
        }
        if (this.getFeature(Feature.DEBUG)) {
            LOG.debug("Returning fresh token " + String.valueOf(tok));
        }
        return tok;
    }

    private void source_untoken(Token tok) {
        if (this.source_token != null) {
            throw new IllegalStateException("Cannot return two tokens");
        }
        this.source_token = tok;
    }

    private boolean isWhite(Token tok) {
        int type = tok.getType();
        return type == 294 || type == 260 || type == 261;
    }

    private Token source_token_nonwhite() throws IOException, LexerException {
        Token tok;
        while (this.isWhite(tok = this.source_token())) {
        }
        return tok;
    }

    private Token source_skipline(boolean white) throws IOException, LexerException {
        Token mark;
        Source s = this.getSource();
        Token tok = s.skipline(white);
        if (tok.getType() == 265 && s.isAutopop() && (mark = this.pop_source(true)) != null) {
            return mark;
        }
        return tok;
    }

    /*
     * Unable to fully structure code
     */
    private boolean macro(Macro m, Token orig) throws IOException, LexerException {
        block37: {
            block34: {
                block35: {
                    block36: {
                        if (!m.isFunctionLike()) break block34;
                        block15: while (true) {
                            tok = this.source_token();
                            switch (tok.getType()) {
                                case 260: 
                                case 261: 
                                case 284: 
                                case 294: {
                                    continue block15;
                                }
                                case 40: {
                                    break block15;
                                }
                                default: {
                                    this.source_untoken(tok);
                                    return false;
                                }
                            }
                            break;
                        }
                        tok = this.source_token_nonwhite();
                        if (tok.getType() == 41 && m.getArgs() <= 0) break block35;
                        args = new ArrayList<Argument>();
                        arg = new Argument();
                        depth = 0;
                        space = false;
                        block16: while (true) {
                            switch (tok.getType()) {
                                case 265: {
                                    this.error(tok, "EOF in macro args");
                                    return false;
                                }
                                case 44: {
                                    if (depth == 0) {
                                        if (m.isVariadic() && args.size() == m.getArgs() - 1) {
                                            arg.addToken(tok);
                                        } else {
                                            args.add(arg);
                                            arg = new Argument();
                                        }
                                    } else {
                                        arg.addToken(tok);
                                    }
                                    space = false;
                                    break;
                                }
                                case 41: {
                                    if (depth == 0) {
                                        args.add(arg);
                                        break block16;
                                    }
                                    --depth;
                                    arg.addToken(tok);
                                    space = false;
                                    break;
                                }
                                case 40: {
                                    ++depth;
                                    arg.addToken(tok);
                                    space = false;
                                    break;
                                }
                                case 260: 
                                case 261: 
                                case 284: 
                                case 294: {
                                    space = true;
                                    break;
                                }
                                default: {
                                    if (space && !arg.isEmpty()) {
                                        arg.addToken(Token.space);
                                    }
                                    arg.addToken(tok);
                                    space = false;
                                }
                            }
                            tok = this.source_token();
                        }
                        if (args.size() == m.getArgs()) break block36;
                        if (!m.isVariadic()) ** GOTO lbl67
                        if (args.size() == m.getArgs() - 1) {
                            args.add(new Argument());
                        } else {
                            this.error(tok, "variadic macro " + m.getName() + " has at least " + (m.getArgs() - 1) + " parameters but given " + args.size() + " args");
                            return false;
lbl67:
                            // 1 sources

                            this.error(tok, "macro " + m.getName() + " has " + m.getArgs() + " parameters but given " + args.size() + " args");
                            return false;
                        }
                    }
                    for (Argument a : args) {
                        a.expand(this);
                    }
                    break block37;
                }
                args = null;
                break block37;
            }
            args = null;
        }
        if (m == Preprocessor.__LINE__) {
            this.push_source(new FixedTokenSource(new Token[]{new Token(272, orig.getLine(), orig.getColumn(), Integer.toString(orig.getLine()), new NumericValue(10, Integer.toString(orig.getLine())))}), true);
        } else if (m == Preprocessor.__FILE__) {
            buf = new StringBuilder("\"");
            name = this.getSource().getName();
            if (name == null) {
                name = "<no file>";
            }
            block18: for (i = 0; i < name.length(); ++i) {
                c = name.charAt(i);
                switch (c) {
                    case '\\': {
                        buf.append("\\\\");
                        continue block18;
                    }
                    case '\"': {
                        buf.append("\\\"");
                        continue block18;
                    }
                    default: {
                        buf.append(c);
                    }
                }
            }
            buf.append("\"");
            text = buf.toString();
            this.push_source(new FixedTokenSource(new Token[]{new Token(292, orig.getLine(), orig.getColumn(), text, text)}), true);
        } else if (m == Preprocessor.__COUNTER__) {
            value = this.counter++;
            this.push_source(new FixedTokenSource(new Token[]{new Token(272, orig.getLine(), orig.getColumn(), Integer.toString(value), new NumericValue(10, Integer.toString(value)))}), true);
        } else {
            this.push_source(new MacroTokenSource(m, args), true);
        }
        return true;
    }

    @Nonnull
    List<Token> expand(@Nonnull List<Token> arg) throws IOException, LexerException {
        ArrayList<Token> expansion = new ArrayList<Token>();
        boolean space = false;
        this.push_source(new FixedTokenSource(arg), false);
        block4: while (true) {
            Token tok = this.expanded_token();
            switch (tok.getType()) {
                case 265: {
                    break block4;
                }
                case 260: 
                case 261: 
                case 294: {
                    space = true;
                    continue block4;
                }
                default: {
                    if (space && !expansion.isEmpty()) {
                        expansion.add(Token.space);
                    }
                    expansion.add(tok);
                    space = false;
                    continue block4;
                }
            }
            break;
        }
        this.pop_source(false);
        return expansion;
    }

    private Token define() throws IOException, LexerException {
        List<String> args;
        Token tok = this.source_token_nonwhite();
        if (tok.getType() != 270) {
            this.error(tok, "Expected identifier");
            return this.source_skipline(false);
        }
        String name = tok.getText();
        if ("defined".equals(name)) {
            this.error(tok, "Cannot redefine name 'defined'");
            return this.source_skipline(false);
        }
        Macro m = new Macro(this.getSource(), name);
        tok = this.source_token();
        if (tok.getType() == 40) {
            block34: {
                tok = this.source_token_nonwhite();
                if (tok.getType() != 41) {
                    args = new ArrayList();
                    while (true) {
                        switch (tok.getType()) {
                            case 270: {
                                args.add(tok.getText());
                                break;
                            }
                            case 264: {
                                args.add("__VA_ARGS__");
                                this.source_untoken(tok);
                                break;
                            }
                            case 265: 
                            case 284: {
                                this.error(tok, "Unterminated macro parameter list");
                                return tok;
                            }
                            default: {
                                this.error(tok, "error in macro parameters: " + tok.getText());
                                return this.source_skipline(false);
                            }
                        }
                        tok = this.source_token_nonwhite();
                        switch (tok.getType()) {
                            case 44: {
                                break;
                            }
                            case 264: {
                                tok = this.source_token_nonwhite();
                                if (tok.getType() != 41) {
                                    this.error(tok, "ellipsis must be on last argument");
                                }
                                m.setVariadic(true);
                                break block34;
                            }
                            case 41: {
                                break block34;
                            }
                            case 265: 
                            case 284: {
                                this.error(tok, "Unterminated macro parameters");
                                return tok;
                            }
                            default: {
                                this.error(tok, "Bad token in macro parameters: " + tok.getText());
                                return this.source_skipline(false);
                            }
                        }
                        tok = this.source_token_nonwhite();
                    }
                }
                assert (tok.getType() == 41) : "Expected ')'";
                args = Collections.emptyList();
            }
            m.setArgs(args);
        } else {
            args = Collections.emptyList();
            this.source_untoken(tok);
        }
        boolean space = false;
        boolean paste = false;
        tok = this.source_token_nonwhite();
        block20: while (true) {
            switch (tok.getType()) {
                case 265: {
                    break block20;
                }
                case 284: {
                    break block20;
                }
                case 260: 
                case 261: 
                case 294: {
                    if (paste) break;
                    space = true;
                    break;
                }
                case 286: {
                    space = false;
                    paste = true;
                    m.addPaste(new Token(297, tok.getLine(), tok.getColumn(), "##", null));
                    break;
                }
                case 35: {
                    int idx;
                    if (space) {
                        m.addToken(Token.space);
                    }
                    space = false;
                    Token la = this.source_token_nonwhite();
                    if (la.getType() == 270 && (idx = args.indexOf(la.getText())) != -1) {
                        m.addToken(new Token(298, la.getLine(), la.getColumn(), "#" + la.getText(), idx));
                        break;
                    }
                    m.addToken(tok);
                    this.source_untoken(la);
                    break;
                }
                case 270: {
                    if (space) {
                        m.addToken(Token.space);
                    }
                    space = false;
                    paste = false;
                    int idx = args.indexOf(tok.getText());
                    if (idx == -1) {
                        m.addToken(tok);
                        break;
                    }
                    m.addToken(new Token(296, tok.getLine(), tok.getColumn(), tok.getText(), idx));
                    break;
                }
                default: {
                    if (space) {
                        m.addToken(Token.space);
                    }
                    space = false;
                    paste = false;
                    m.addToken(tok);
                }
            }
            tok = this.source_token();
        }
        if (this.getFeature(Feature.DEBUG)) {
            LOG.debug("Defined macro " + String.valueOf(m));
        }
        this.addMacro(m);
        return tok;
    }

    @Nonnull
    private Token undef() throws IOException, LexerException {
        Token tok = this.source_token_nonwhite();
        if (tok.getType() != 270) {
            this.error(tok, "Expected identifier, not " + tok.getText());
            if (tok.getType() == 284 || tok.getType() == 265) {
                return tok;
            }
        } else {
            Macro m = this.getMacro(tok.getText());
            if (m != null) {
                this.macros.remove(m.getName());
            }
        }
        return this.source_skipline(true);
    }

    protected boolean include(@Nonnull VirtualFile file) throws IOException {
        if (!file.isFile()) {
            return false;
        }
        if (this.getFeature(Feature.DEBUG)) {
            LOG.debug("pp: including " + String.valueOf(file));
        }
        this.includes.add(file);
        this.push_source(file.getSource(), true);
        return true;
    }

    protected boolean include(@Nonnull Iterable<String> path, @Nonnull String name) throws IOException {
        for (String dir : path) {
            VirtualFile file = this.getFileSystem().getFile(dir, name);
            if (!this.include(file)) continue;
            return true;
        }
        return false;
    }

    private void include(@CheckForNull String parent, int line, @Nonnull String name, boolean quoted, boolean next) throws IOException, LexerException {
        if (name.startsWith("/")) {
            VirtualFile file = this.filesystem.getFile(name);
            if (this.include(file)) {
                return;
            }
            StringBuilder buf = new StringBuilder();
            buf.append("File not found: ").append(name);
            this.error(line, 0, buf.toString());
            return;
        }
        VirtualFile pdir = null;
        if (quoted) {
            VirtualFile ifile;
            if (parent != null) {
                VirtualFile pfile = this.filesystem.getFile(parent);
                pdir = pfile.getParentFile();
            }
            if (pdir != null && this.include(ifile = pdir.getChildFile(name))) {
                return;
            }
            if (this.include(this.quoteincludepath, name)) {
                return;
            }
        } else {
            String headerName;
            String frameworkName;
            String headerPath;
            int idx = name.indexOf(47);
            if (idx != -1 && this.include(this.frameworkspath, headerPath = (frameworkName = name.substring(0, idx)) + ".framework/Headers/" + (headerName = name.substring(idx + 1)))) {
                return;
            }
        }
        if (this.include(this.sysincludepath, name)) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        buf.append("File not found: ").append(name);
        buf.append(" in");
        if (quoted) {
            buf.append(" .").append('(').append(pdir).append(')');
            for (String dir : this.quoteincludepath) {
                buf.append(" ").append(dir);
            }
        }
        for (String dir : this.sysincludepath) {
            buf.append(" ").append(dir);
        }
        this.error(line, 0, buf.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    private Token include(boolean next) throws IOException, LexerException {
        LexerSource lexer = (LexerSource)this.source;
        try {
            Token token;
            boolean quoted;
            String name;
            lexer.setInclude(true);
            Token tok = this.token_nonwhite();
            if (tok.getType() == 292) {
                StringBuilder buf = new StringBuilder((String)tok.getValue());
                block14: while (true) {
                    tok = this.token_nonwhite();
                    switch (tok.getType()) {
                        case 292: {
                            buf.append((String)tok.getValue());
                            continue block14;
                        }
                        case 265: 
                        case 284: {
                            break block14;
                        }
                        default: {
                            this.warning(tok, "Unexpected token on #include line");
                            Token token2 = this.source_skipline(false);
                            return token2;
                        }
                    }
                    break;
                }
                name = buf.toString();
                quoted = true;
            } else if (tok.getType() == 269) {
                name = (String)tok.getValue();
                quoted = false;
                tok = this.source_skipline(true);
            } else {
                this.error(tok, "Expected string or header, not " + tok.getText());
                switch (tok.getType()) {
                    case 265: 
                    case 284: {
                        Token token3 = tok;
                        return token3;
                    }
                }
                Token token4 = this.source_skipline(false);
                return token4;
            }
            this.include(this.source.getPath(), tok.getLine(), name, quoted, next);
            if (this.getFeature(Feature.LINEMARKERS)) {
                token = this.line_token(1, this.source.getName(), " 1");
                return token;
            }
            token = tok;
            return token;
        }
        finally {
            lexer.setInclude(false);
        }
    }

    protected void pragma_once(@Nonnull Token name) throws IOException, LexerException {
        Token mark;
        Source s = this.source;
        if (!this.onceseenpaths.add(s.getPath()) && (mark = this.pop_source(true)) != null) {
            this.push_source(new FixedTokenSource(Arrays.asList(mark)), true);
        }
    }

    protected void pragma(@Nonnull Token name, @Nonnull List<Token> value) throws IOException, LexerException {
        if (this.getFeature(Feature.PRAGMA_ONCE) && "once".equals(name.getText())) {
            this.pragma_once(name);
            return;
        }
        this.warning(name, "Unknown #pragma: " + name.getText());
    }

    @Nonnull
    private Token pragma() throws IOException, LexerException {
        Token name;
        Token tok;
        block12: while (true) {
            tok = this.source_token();
            switch (tok.getType()) {
                case 265: {
                    this.warning(tok, "End of file in #pragma");
                    return tok;
                }
                case 284: {
                    this.warning(tok, "Empty #pragma");
                    return tok;
                }
                case 260: 
                case 261: 
                case 294: {
                    continue block12;
                }
                case 270: {
                    name = tok;
                    break block12;
                }
                default: {
                    this.warning(tok, "Illegal #pragma " + tok.getText());
                    return this.source_skipline(false);
                }
            }
            break;
        }
        ArrayList<Token> value = new ArrayList<Token>();
        block13: while (true) {
            tok = this.source_token();
            switch (tok.getType()) {
                case 265: {
                    this.warning(tok, "End of file in #pragma");
                    break block13;
                }
                case 284: {
                    break block13;
                }
                case 260: 
                case 261: {
                    continue block13;
                }
                case 294: {
                    value.add(tok);
                    continue block13;
                }
                default: {
                    value.add(tok);
                    continue block13;
                }
            }
            break;
        }
        this.pragma(name, value);
        return tok;
    }

    private void error(@Nonnull Token pptok, boolean is_error) throws IOException, LexerException {
        StringBuilder buf = new StringBuilder();
        buf.append('#').append(pptok.getText()).append(' ');
        Token tok = this.source_token_nonwhite();
        block3: while (true) {
            switch (tok.getType()) {
                case 265: 
                case 284: {
                    break block3;
                }
                default: {
                    buf.append(tok.getText());
                    tok = this.source_token();
                    continue block3;
                }
            }
            break;
        }
        if (is_error) {
            this.error(pptok, buf.toString());
        } else {
            this.warning(pptok, buf.toString());
        }
    }

    @Nonnull
    private Token expanded_token() throws IOException, LexerException {
        Token tok;
        while ((tok = this.source_token()).getType() == 270) {
            Macro m = this.getMacro(tok.getText());
            if (m == null) {
                return tok;
            }
            if (this.source.isExpanding(m)) {
                return tok;
            }
            if (this.macro(m, tok)) continue;
        }
        return tok;
    }

    @Nonnull
    private Token expanded_token_nonwhite() throws IOException, LexerException {
        Token tok;
        while (this.isWhite(tok = this.expanded_token())) {
        }
        return tok;
    }

    @Nonnull
    private Token expr_token() throws IOException, LexerException {
        Token tok = this.expr_token;
        if (tok != null) {
            this.expr_token = null;
        } else {
            tok = this.expanded_token_nonwhite();
            if (tok.getType() == 270 && tok.getText().equals("defined")) {
                Token la = this.source_token_nonwhite();
                boolean paren = false;
                if (la.getType() == 40) {
                    paren = true;
                    la = this.source_token_nonwhite();
                }
                if (la.getType() != 270) {
                    this.error(la, "defined() needs identifier, not " + la.getText());
                    tok = new Token(272, la.getLine(), la.getColumn(), "0", new NumericValue(10, "0"));
                } else {
                    tok = this.macros.containsKey(la.getText()) ? new Token(272, la.getLine(), la.getColumn(), "1", new NumericValue(10, "1")) : new Token(272, la.getLine(), la.getColumn(), "0", new NumericValue(10, "0"));
                }
                if (paren && (la = this.source_token_nonwhite()).getType() != 41) {
                    this.expr_untoken(la);
                    this.error(la, "Missing ) in defined(). Got " + la.getText());
                }
            }
        }
        return tok;
    }

    private void expr_untoken(@Nonnull Token tok) throws LexerException {
        if (this.expr_token != null) {
            throw new InternalException("Cannot unget two expression tokens.");
        }
        this.expr_token = tok;
    }

    private int expr_priority(@Nonnull Token op) {
        switch (op.getType()) {
            case 47: {
                return 11;
            }
            case 37: {
                return 11;
            }
            case 42: {
                return 11;
            }
            case 43: {
                return 10;
            }
            case 45: {
                return 10;
            }
            case 279: {
                return 9;
            }
            case 289: {
                return 9;
            }
            case 60: {
                return 8;
            }
            case 62: {
                return 8;
            }
            case 275: {
                return 8;
            }
            case 267: {
                return 8;
            }
            case 266: {
                return 7;
            }
            case 283: {
                return 7;
            }
            case 38: {
                return 6;
            }
            case 94: {
                return 5;
            }
            case 124: {
                return 4;
            }
            case 273: {
                return 3;
            }
            case 277: {
                return 2;
            }
            case 63: {
                return 1;
            }
        }
        return 0;
    }

    private int expr_char(Token token) {
        Object value = token.getValue();
        if (value instanceof Character) {
            return ((Character)value).charValue();
        }
        String text = String.valueOf(value);
        if (text.length() == 0) {
            return 0;
        }
        return text.charAt(0);
    }

    private long expr(int priority) throws IOException, LexerException {
        Token op;
        long lhs;
        block35: {
            Token tok = this.expr_token();
            switch (tok.getType()) {
                case 40: {
                    lhs = this.expr(0);
                    tok = this.expr_token();
                    if (tok.getType() == 41) break;
                    this.expr_untoken(tok);
                    this.error(tok, "Missing ) in expression. Got " + tok.getText());
                    return 0L;
                }
                case 126: {
                    lhs = this.expr(11) ^ 0xFFFFFFFFFFFFFFFFL;
                    break;
                }
                case 33: {
                    lhs = this.expr(11) == 0L ? 1L : 0L;
                    break;
                }
                case 45: {
                    lhs = -this.expr(11);
                    break;
                }
                case 272: {
                    NumericValue value = (NumericValue)tok.getValue();
                    lhs = value.longValue();
                    break;
                }
                case 259: {
                    lhs = this.expr_char(tok);
                    break;
                }
                case 270: {
                    if (this.warnings.contains((Object)Warning.UNDEF)) {
                        this.warning(tok, "Undefined token '" + tok.getText() + "' encountered in conditional.");
                    }
                    lhs = 0L;
                    break;
                }
                default: {
                    this.expr_untoken(tok);
                    this.error(tok, "Bad token in expression: " + tok.getText());
                    return 0L;
                }
            }
            block30: while (true) {
                int pri;
                if ((pri = this.expr_priority(op = this.expr_token())) == 0 || priority >= pri) break block35;
                long rhs = this.expr(pri);
                switch (op.getType()) {
                    case 47: {
                        if (rhs == 0L) {
                            this.error(op, "Division by zero");
                            lhs = 0L;
                            continue block30;
                        }
                        lhs /= rhs;
                        continue block30;
                    }
                    case 37: {
                        if (rhs == 0L) {
                            this.error(op, "Modulus by zero");
                            lhs = 0L;
                            continue block30;
                        }
                        lhs %= rhs;
                        continue block30;
                    }
                    case 42: {
                        lhs *= rhs;
                        continue block30;
                    }
                    case 43: {
                        lhs += rhs;
                        continue block30;
                    }
                    case 45: {
                        lhs -= rhs;
                        continue block30;
                    }
                    case 60: {
                        lhs = lhs < rhs ? 1L : 0L;
                        continue block30;
                    }
                    case 62: {
                        lhs = lhs > rhs ? 1L : 0L;
                        continue block30;
                    }
                    case 38: {
                        lhs &= rhs;
                        continue block30;
                    }
                    case 94: {
                        lhs ^= rhs;
                        continue block30;
                    }
                    case 124: {
                        lhs |= rhs;
                        continue block30;
                    }
                    case 279: {
                        lhs <<= (int)rhs;
                        continue block30;
                    }
                    case 289: {
                        lhs >>= (int)rhs;
                        continue block30;
                    }
                    case 275: {
                        lhs = lhs <= rhs ? 1L : 0L;
                        continue block30;
                    }
                    case 267: {
                        lhs = lhs >= rhs ? 1L : 0L;
                        continue block30;
                    }
                    case 266: {
                        lhs = lhs == rhs ? 1L : 0L;
                        continue block30;
                    }
                    case 283: {
                        lhs = lhs != rhs ? 1L : 0L;
                        continue block30;
                    }
                    case 273: {
                        lhs = lhs != 0L && rhs != 0L ? 1L : 0L;
                        continue block30;
                    }
                    case 277: {
                        lhs = lhs != 0L || rhs != 0L ? 1L : 0L;
                        continue block30;
                    }
                    case 63: {
                        tok = this.expr_token();
                        if (tok.getType() != 58) {
                            this.expr_untoken(tok);
                            this.error(tok, "Missing : in conditional expression. Got " + tok.getText());
                            return 0L;
                        }
                        long falseResult = this.expr(0);
                        lhs = lhs != 0L ? rhs : falseResult;
                        continue block30;
                    }
                }
                break;
            }
            this.error(op, "Unexpected operator " + op.getText());
            return 0L;
        }
        this.expr_untoken(op);
        return lhs;
    }

    @Nonnull
    private Token toWhitespace(@Nonnull Token tok) {
        String text = tok.getText();
        int len = text.length();
        boolean cr = false;
        int nls = 0;
        block5: for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\r': {
                    cr = true;
                    ++nls;
                    continue block5;
                }
                case '\n': {
                    if (cr) {
                        cr = false;
                        continue block5;
                    }
                }
                case '\u000b': 
                case '\f': 
                case '\u0085': 
                case '\u2028': 
                case '\u2029': {
                    cr = false;
                    ++nls;
                }
            }
        }
        char[] cbuf = new char[nls];
        Arrays.fill(cbuf, '\n');
        return new Token(294, tok.getLine(), tok.getColumn(), new String(cbuf));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    private Token _token() throws IOException, LexerException {
        block38: while (true) {
            Token tok;
            if (!this.isActive()) {
                Source s = this.getSource();
                if (s == null) {
                    Token t = this.next_source();
                    if (t.getType() == 299 && !this.getFeature(Feature.LINEMARKERS)) continue;
                    return t;
                }
                try {
                    s.setActive(false);
                    tok = this.source_token();
                }
                finally {
                    s.setActive(true);
                }
                switch (tok.getType()) {
                    case 265: 
                    case 268: 
                    case 284: {
                        break;
                    }
                    case 294: {
                        return tok;
                    }
                    case 260: 
                    case 261: {
                        if (this.getFeature(Feature.KEEPALLCOMMENTS)) {
                            return tok;
                        }
                        if (!this.isActive()) {
                            return this.toWhitespace(tok);
                        }
                        if (this.getFeature(Feature.KEEPCOMMENTS)) {
                            return tok;
                        }
                        return this.toWhitespace(tok);
                    }
                    default: {
                        return this.source_skipline(false);
                    }
                }
            } else {
                tok = this.source_token();
            }
            block6 : switch (tok.getType()) {
                case 265: {
                    return tok;
                }
                case 284: 
                case 294: {
                    return tok;
                }
                case 260: 
                case 261: {
                    return tok;
                }
                case 33: 
                case 37: 
                case 38: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 64: 
                case 91: 
                case 93: 
                case 94: 
                case 96: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 257: 
                case 258: 
                case 259: 
                case 262: 
                case 263: 
                case 264: 
                case 266: 
                case 267: 
                case 269: 
                case 271: 
                case 273: 
                case 275: 
                case 277: 
                case 279: 
                case 280: 
                case 281: 
                case 282: 
                case 283: 
                case 285: 
                case 287: 
                case 288: 
                case 289: 
                case 290: 
                case 291: 
                case 292: 
                case 293: 
                case 295: {
                    return tok;
                }
                case 272: {
                    return tok;
                }
                case 270: {
                    Macro m = this.getMacro(tok.getText());
                    if (m == null) {
                        return tok;
                    }
                    if (this.source.isExpanding(m)) {
                        return tok;
                    }
                    if (this.macro(m, tok)) continue block38;
                    return tok;
                }
                case 299: {
                    if (!this.getFeature(Feature.LINEMARKERS)) continue block38;
                    return tok;
                }
                case 300: {
                    if (this.getFeature(Feature.CSYNTAX)) {
                        this.error(tok, String.valueOf(tok.getValue()));
                    }
                    return tok;
                }
                default: {
                    throw new InternalException("Bad token " + String.valueOf(tok));
                }
                case 268: {
                    tok = this.source_token_nonwhite();
                    switch (tok.getType()) {
                        case 284: {
                            break block6;
                        }
                        case 270: {
                            break;
                        }
                        default: {
                            this.error(tok, "Preprocessor directive not a word " + tok.getText());
                            return this.source_skipline(false);
                        }
                    }
                    PreprocessorCommand ppcmd = PreprocessorCommand.forText(tok.getText());
                    if (ppcmd == null) {
                        this.error(tok, "Unknown preprocessor directive " + tok.getText());
                        return this.source_skipline(false);
                    }
                    switch (ppcmd) {
                        case PP_DEFINE: {
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            return this.define();
                        }
                        case PP_UNDEF: {
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            return this.undef();
                        }
                        case PP_INCLUDE: {
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            return this.include(false);
                        }
                        case PP_INCLUDE_NEXT: {
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            if (!this.getFeature(Feature.INCLUDENEXT)) {
                                this.error(tok, "Directive include_next not enabled");
                                return this.source_skipline(false);
                            }
                            return this.include(true);
                        }
                        case PP_WARNING: 
                        case PP_ERROR: {
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            this.error(tok, ppcmd == PreprocessorCommand.PP_ERROR);
                            break block6;
                        }
                        case PP_IF: {
                            this.push_state();
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            this.expr_token = null;
                            this.states.peek().setActive(this.expr(0) != 0L);
                            tok = this.expr_token();
                            if (tok.getType() == 284) {
                                return tok;
                            }
                            return this.source_skipline(true);
                        }
                        case PP_ELIF: {
                            State state = this.states.peek();
                            if (state.sawElse()) {
                                this.error(tok, "#elif after #else");
                                return this.source_skipline(false);
                            }
                            if (!state.isParentActive()) {
                                return this.source_skipline(false);
                            }
                            if (state.isActive()) {
                                state.setParentActive(false);
                                state.setActive(false);
                                return this.source_skipline(false);
                            }
                            this.expr_token = null;
                            state.setActive(this.expr(0) != 0L);
                            tok = this.expr_token();
                            if (tok.getType() == 284) {
                                return tok;
                            }
                            return this.source_skipline(true);
                        }
                        case PP_ELSE: {
                            State state = this.states.peek();
                            if (state.sawElse()) {
                                this.error(tok, "#else after #else");
                                return this.source_skipline(false);
                            }
                            state.setSawElse();
                            state.setActive(!state.isActive());
                            return this.source_skipline(this.warnings.contains((Object)Warning.ENDIF_LABELS));
                        }
                        case PP_IFDEF: {
                            this.push_state();
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            tok = this.source_token_nonwhite();
                            if (tok.getType() != 270) {
                                this.error(tok, "Expected identifier, not " + tok.getText());
                                return this.source_skipline(false);
                            }
                            String text = tok.getText();
                            boolean exists = this.macros.containsKey(text);
                            this.states.peek().setActive(exists);
                            return this.source_skipline(true);
                        }
                        case PP_IFNDEF: {
                            String text;
                            boolean exists;
                            this.push_state();
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            tok = this.source_token_nonwhite();
                            if (tok.getType() != 270) {
                                this.error(tok, "Expected identifier, not " + tok.getText());
                                return this.source_skipline(false);
                            }
                            this.states.peek().setActive(!(exists = this.macros.containsKey(text = tok.getText())));
                            return this.source_skipline(true);
                        }
                        case PP_ENDIF: {
                            this.pop_state();
                            return this.source_skipline(this.warnings.contains((Object)Warning.ENDIF_LABELS));
                        }
                        case PP_LINE: {
                            return this.source_skipline(false);
                        }
                        case PP_PRAGMA: {
                            if (!this.isActive()) {
                                return this.source_skipline(false);
                            }
                            return this.pragma();
                        }
                    }
                    throw new InternalException("Internal error: Unknown directive " + String.valueOf(tok));
                }
            }
        }
    }

    @Nonnull
    private Token token_nonwhite() throws IOException, LexerException {
        Token tok;
        while (this.isWhite(tok = this._token())) {
        }
        return tok;
    }

    @Nonnull
    public Token token() throws IOException, LexerException {
        Token tok = this._token();
        if (this.getFeature(Feature.DEBUG)) {
            LOG.debug("pp: Returning " + String.valueOf(tok));
        }
        return tok;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Source s = this.getSource(); s != null; s = s.getParent()) {
            buf.append(" -> ").append(String.valueOf(s)).append("\n");
        }
        TreeMap<String, Macro> macros = new TreeMap<String, Macro>(this.getMacros());
        for (Macro macro : macros.values()) {
            buf.append("#").append("macro ").append(macro).append("\n");
        }
        return buf.toString();
    }

    @Override
    public void close() throws IOException {
        for (Source s = this.source; s != null; s = s.getParent()) {
            s.close();
        }
        for (Source s : this.inputs) {
            s.close();
        }
    }
}

