package amp_libs.org.antlr.v4.runtime;

import amp_libs.org.antlr.v4.runtime.atn.ATN;
import amp_libs.org.antlr.v4.runtime.atn.ATNDeserializationOptions;
import amp_libs.org.antlr.v4.runtime.atn.ATNDeserializer;
import amp_libs.org.antlr.v4.runtime.atn.ATNSimulator;
import amp_libs.org.antlr.v4.runtime.atn.ATNState;
import amp_libs.org.antlr.v4.runtime.atn.ParseInfo;
import amp_libs.org.antlr.v4.runtime.atn.ParserATNSimulator;
import amp_libs.org.antlr.v4.runtime.atn.PredictionMode;
import amp_libs.org.antlr.v4.runtime.atn.ProfilingATNSimulator;
import amp_libs.org.antlr.v4.runtime.atn.RuleTransition;
import amp_libs.org.antlr.v4.runtime.dfa.DFA;
import amp_libs.org.antlr.v4.runtime.misc.IntegerStack;
import amp_libs.org.antlr.v4.runtime.misc.IntervalSet;
import amp_libs.org.antlr.v4.runtime.tree.ErrorNode;
import amp_libs.org.antlr.v4.runtime.tree.ErrorNodeImpl;
import amp_libs.org.antlr.v4.runtime.tree.ParseTreeListener;
import amp_libs.org.antlr.v4.runtime.tree.TerminalNode;
import amp_libs.org.antlr.v4.runtime.tree.TerminalNodeImpl;
import amp_libs.org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import amp_libs.org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Parser extends Recognizer<Token, ParserATNSimulator> {
   private ATN bypassAltsAtnCache;
   protected ANTLRErrorStrategy _errHandler = new DefaultErrorStrategy();
   protected TokenStream _input;
   protected final IntegerStack _precedenceStack = new IntegerStack();
   protected ParserRuleContext _ctx;
   protected boolean _buildParseTrees;
   private Parser.TraceListener _tracer;
   protected List<ParseTreeListener> _parseListeners;
   protected int _syntaxErrors;
   protected boolean matchedEOF;

   public Parser(TokenStream input) {
      this._precedenceStack.push(0);
      this._buildParseTrees = true;
      this.setInputStream(input);
   }

   public void reset() {
      if (this.getInputStream() != null) {
         this.getInputStream().seek(0);
      }

      this._errHandler.reset(this);
      this._ctx = null;
      this._syntaxErrors = 0;
      this.matchedEOF = false;
      this.setTrace(false);
      this._precedenceStack.clear();
      this._precedenceStack.push(0);
      ATNSimulator interpreter = this.getInterpreter();
      if (interpreter != null) {
         interpreter.reset();
      }
   }

   public Token match(int ttype) throws RecognitionException {
      Token t = this.getCurrentToken();
      if (t.getType() == ttype) {
         if (ttype == -1) {
            this.matchedEOF = true;
         }

         this._errHandler.reportMatch(this);
         this.consume();
      } else {
         t = this._errHandler.recoverInline(this);
         if (this._buildParseTrees && t.getTokenIndex() == -1) {
            this._ctx.addErrorNode(this.createErrorNode(this._ctx, t));
         }
      }

      return t;
   }

   public Token matchWildcard() throws RecognitionException {
      Token t = this.getCurrentToken();
      if (t.getType() > 0) {
         this._errHandler.reportMatch(this);
         this.consume();
      } else {
         t = this._errHandler.recoverInline(this);
         if (this._buildParseTrees && t.getTokenIndex() == -1) {
            this._ctx.addErrorNode(this.createErrorNode(this._ctx, t));
         }
      }

      return t;
   }

   public void setBuildParseTree(boolean buildParseTrees) {
      this._buildParseTrees = buildParseTrees;
   }

   public boolean getBuildParseTree() {
      return this._buildParseTrees;
   }

   public void setTrimParseTree(boolean trimParseTrees) {
      if (trimParseTrees) {
         if (this.getTrimParseTree()) {
            return;
         }

         this.addParseListener(Parser.TrimToSizeListener.INSTANCE);
      } else {
         this.removeParseListener(Parser.TrimToSizeListener.INSTANCE);
      }
   }

   public boolean getTrimParseTree() {
      return this.getParseListeners().contains(Parser.TrimToSizeListener.INSTANCE);
   }

   public List<ParseTreeListener> getParseListeners() {
      List<ParseTreeListener> listeners = this._parseListeners;
      return listeners == null ? Collections.emptyList() : listeners;
   }

   public void addParseListener(ParseTreeListener listener) {
      if (listener == null) {
         throw new NullPointerException("listener");
      } else {
         if (this._parseListeners == null) {
            this._parseListeners = new ArrayList<>();
         }

         this._parseListeners.add(listener);
      }
   }

   public void removeParseListener(ParseTreeListener listener) {
      if (this._parseListeners != null && this._parseListeners.remove(listener) && this._parseListeners.isEmpty()) {
         this._parseListeners = null;
      }
   }

   public void removeParseListeners() {
      this._parseListeners = null;
   }

   protected void triggerEnterRuleEvent() {
      for (ParseTreeListener listener : this._parseListeners) {
         listener.enterEveryRule(this._ctx);
         this._ctx.enterRule(listener);
      }
   }

   protected void triggerExitRuleEvent() {
      for (int i = this._parseListeners.size() - 1; i >= 0; i--) {
         ParseTreeListener listener = this._parseListeners.get(i);
         this._ctx.exitRule(listener);
         listener.exitEveryRule(this._ctx);
      }
   }

   public int getNumberOfSyntaxErrors() {
      return this._syntaxErrors;
   }

   @Override
   public TokenFactory<?> getTokenFactory() {
      return this._input.getTokenSource().getTokenFactory();
   }

   @Override
   public void setTokenFactory(TokenFactory<?> factory) {
      this._input.getTokenSource().setTokenFactory(factory);
   }

   public ATN getATNWithBypassAlts() {
      String serializedAtn = this.getSerializedATN();
      if (serializedAtn == null) {
         throw new UnsupportedOperationException("The current parser does not support an ATN with bypass alternatives.");
      } else {
         synchronized (this) {
            if (this.bypassAltsAtnCache != null) {
               return this.bypassAltsAtnCache;
            } else {
               ATNDeserializationOptions deserializationOptions = new ATNDeserializationOptions();
               deserializationOptions.setGenerateRuleBypassTransitions(true);
               this.bypassAltsAtnCache = new ATNDeserializer(deserializationOptions).deserialize(serializedAtn.toCharArray());
               return this.bypassAltsAtnCache;
            }
         }
      }
   }

   public ParseTreePattern compileParseTreePattern(String pattern, int patternRuleIndex) {
      if (this.getTokenStream() != null) {
         TokenSource tokenSource = this.getTokenStream().getTokenSource();
         if (tokenSource instanceof Lexer) {
            Lexer lexer = (Lexer)tokenSource;
            return this.compileParseTreePattern(pattern, patternRuleIndex, lexer);
         }
      }

      throw new UnsupportedOperationException("Parser can't discover a lexer to use");
   }

   public ParseTreePattern compileParseTreePattern(String pattern, int patternRuleIndex, Lexer lexer) {
      ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, this);
      return m.compile(pattern, patternRuleIndex);
   }

   public ANTLRErrorStrategy getErrorHandler() {
      return this._errHandler;
   }

   public void setErrorHandler(ANTLRErrorStrategy handler) {
      this._errHandler = handler;
   }

   public TokenStream getInputStream() {
      return this.getTokenStream();
   }

   @Override
   public final void setInputStream(IntStream input) {
      this.setTokenStream((TokenStream)input);
   }

   public TokenStream getTokenStream() {
      return this._input;
   }

   public void setTokenStream(TokenStream input) {
      this._input = null;
      this.reset();
      this._input = input;
   }

   public Token getCurrentToken() {
      return this._input.LT(1);
   }

   public final void notifyErrorListeners(String msg) {
      this.notifyErrorListeners(this.getCurrentToken(), msg, null);
   }

   public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException e) {
      this._syntaxErrors++;
      int line = -1;
      int charPositionInLine = -1;
      line = offendingToken.getLine();
      charPositionInLine = offendingToken.getCharPositionInLine();
      ANTLRErrorListener listener = this.getErrorListenerDispatch();
      listener.syntaxError(this, offendingToken, line, charPositionInLine, msg, e);
   }

   public Token consume() {
      Token o = this.getCurrentToken();
      if (o.getType() != -1) {
         this.getInputStream().consume();
      }

      boolean hasListener = this._parseListeners != null && !this._parseListeners.isEmpty();
      if (this._buildParseTrees || hasListener) {
         if (this._errHandler.inErrorRecoveryMode(this)) {
            ErrorNode node = this._ctx.addErrorNode(this.createErrorNode(this._ctx, o));
            if (this._parseListeners != null) {
               for (ParseTreeListener listener : this._parseListeners) {
                  listener.visitErrorNode(node);
               }
            }
         } else {
            TerminalNode node = this._ctx.addChild(this.createTerminalNode(this._ctx, o));
            if (this._parseListeners != null) {
               for (ParseTreeListener listener : this._parseListeners) {
                  listener.visitTerminal(node);
               }
            }
         }
      }

      return o;
   }

   public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
      return new TerminalNodeImpl(t);
   }

   public ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
      return new ErrorNodeImpl(t);
   }

   protected void addContextToParseTree() {
      ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
      if (parent != null) {
         parent.addChild(this._ctx);
      }
   }

   public void enterRule(ParserRuleContext localctx, int state, int ruleIndex) {
      this.setState(state);
      this._ctx = localctx;
      this._ctx.start = this._input.LT(1);
      if (this._buildParseTrees) {
         this.addContextToParseTree();
      }

      if (this._parseListeners != null) {
         this.triggerEnterRuleEvent();
      }
   }

   public void exitRule() {
      if (this.matchedEOF) {
         this._ctx.stop = this._input.LT(1);
      } else {
         this._ctx.stop = this._input.LT(-1);
      }

      if (this._parseListeners != null) {
         this.triggerExitRuleEvent();
      }

      this.setState(this._ctx.invokingState);
      this._ctx = (ParserRuleContext)this._ctx.parent;
   }

   public void enterOuterAlt(ParserRuleContext localctx, int altNum) {
      localctx.setAltNumber(altNum);
      if (this._buildParseTrees && this._ctx != localctx) {
         ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
         if (parent != null) {
            parent.removeLastChild();
            parent.addChild(localctx);
         }
      }

      this._ctx = localctx;
   }

   public final int getPrecedence() {
      return this._precedenceStack.isEmpty() ? -1 : this._precedenceStack.peek();
   }

   @Deprecated
   public void enterRecursionRule(ParserRuleContext localctx, int ruleIndex) {
      this.enterRecursionRule(localctx, this.getATN().ruleToStartState[ruleIndex].stateNumber, ruleIndex, 0);
   }

   public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
      this.setState(state);
      this._precedenceStack.push(precedence);
      this._ctx = localctx;
      this._ctx.start = this._input.LT(1);
      if (this._parseListeners != null) {
         this.triggerEnterRuleEvent();
      }
   }

   public void pushNewRecursionContext(ParserRuleContext localctx, int state, int ruleIndex) {
      ParserRuleContext previous = this._ctx;
      previous.parent = localctx;
      previous.invokingState = state;
      previous.stop = this._input.LT(-1);
      this._ctx = localctx;
      this._ctx.start = previous.start;
      if (this._buildParseTrees) {
         this._ctx.addChild(previous);
      }

      if (this._parseListeners != null) {
         this.triggerEnterRuleEvent();
      }
   }

   public void unrollRecursionContexts(ParserRuleContext _parentctx) {
      this._precedenceStack.pop();
      this._ctx.stop = this._input.LT(-1);
      ParserRuleContext retctx = this._ctx;
      if (this._parseListeners != null) {
         while (this._ctx != _parentctx) {
            this.triggerExitRuleEvent();
            this._ctx = (ParserRuleContext)this._ctx.parent;
         }
      } else {
         this._ctx = _parentctx;
      }

      retctx.parent = _parentctx;
      if (this._buildParseTrees && _parentctx != null) {
         _parentctx.addChild(retctx);
      }
   }

   public ParserRuleContext getInvokingContext(int ruleIndex) {
      for (ParserRuleContext p = this._ctx; p != null; p = (ParserRuleContext)p.parent) {
         if (p.getRuleIndex() == ruleIndex) {
            return p;
         }
      }

      return null;
   }

   public ParserRuleContext getContext() {
      return this._ctx;
   }

   public void setContext(ParserRuleContext ctx) {
      this._ctx = ctx;
   }

   @Override
   public boolean precpred(RuleContext localctx, int precedence) {
      return precedence >= this._precedenceStack.peek();
   }

   public boolean inContext(String context) {
      return false;
   }

   public boolean isExpectedToken(int symbol) {
      ATN atn = this.getInterpreter().atn;
      ParserRuleContext ctx = this._ctx;
      ATNState s = atn.states.get(this.getState());
      IntervalSet following = atn.nextTokens(s);
      if (following.contains(symbol)) {
         return true;
      } else if (!following.contains(-2)) {
         return false;
      } else {
         while (ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
            ATNState invokingState = atn.states.get(ctx.invokingState);
            RuleTransition rt = (RuleTransition)invokingState.transition(0);
            following = atn.nextTokens(rt.followState);
            if (following.contains(symbol)) {
               return true;
            }

            ctx = (ParserRuleContext)ctx.parent;
         }

         return following.contains(-2) && symbol == -1;
      }
   }

   public boolean isMatchedEOF() {
      return this.matchedEOF;
   }

   public IntervalSet getExpectedTokens() {
      return this.getATN().getExpectedTokens(this.getState(), this.getContext());
   }

   public IntervalSet getExpectedTokensWithinCurrentRule() {
      ATN atn = this.getInterpreter().atn;
      ATNState s = atn.states.get(this.getState());
      return atn.nextTokens(s);
   }

   public int getRuleIndex(String ruleName) {
      Integer ruleIndex = this.getRuleIndexMap().get(ruleName);
      return ruleIndex != null ? ruleIndex : -1;
   }

   public ParserRuleContext getRuleContext() {
      return this._ctx;
   }

   public List<String> getRuleInvocationStack() {
      return this.getRuleInvocationStack(this._ctx);
   }

   public List<String> getRuleInvocationStack(RuleContext p) {
      String[] ruleNames = this.getRuleNames();
      List<String> stack = new ArrayList<>();

      while (p != null) {
         int ruleIndex = p.getRuleIndex();
         if (ruleIndex < 0) {
            stack.add("n/a");
         } else {
            stack.add(ruleNames[ruleIndex]);
         }

         p = p.parent;
      }

      return stack;
   }

   public List<String> getDFAStrings() {
      synchronized (this._interp.decisionToDFA) {
         List<String> s = new ArrayList<>();

         for (int d = 0; d < this._interp.decisionToDFA.length; d++) {
            DFA dfa = this._interp.decisionToDFA[d];
            s.add(dfa.toString(this.getVocabulary()));
         }

         return s;
      }
   }

   public void dumpDFA() {
      this.dumpDFA(System.out);
   }

   public void dumpDFA(PrintStream dumpStream) {
      synchronized (this._interp.decisionToDFA) {
         boolean seenOne = false;

         for (int d = 0; d < this._interp.decisionToDFA.length; d++) {
            DFA dfa = this._interp.decisionToDFA[d];
            if (!dfa.states.isEmpty()) {
               if (seenOne) {
                  dumpStream.println();
               }

               dumpStream.println("Decision " + dfa.decision + ":");
               dumpStream.print(dfa.toString(this.getVocabulary()));
               seenOne = true;
            }
         }
      }
   }

   public String getSourceName() {
      return this._input.getSourceName();
   }

   @Override
   public ParseInfo getParseInfo() {
      ParserATNSimulator interp = this.getInterpreter();
      return interp instanceof ProfilingATNSimulator ? new ParseInfo((ProfilingATNSimulator)interp) : null;
   }

   public void setProfile(boolean profile) {
      ParserATNSimulator interp = this.getInterpreter();
      PredictionMode saveMode = interp.getPredictionMode();
      if (profile) {
         if (!(interp instanceof ProfilingATNSimulator)) {
            this.setInterpreter(new ProfilingATNSimulator(this));
         }
      } else if (interp instanceof ProfilingATNSimulator) {
         ParserATNSimulator sim = new ParserATNSimulator(this, this.getATN(), interp.decisionToDFA, interp.getSharedContextCache());
         this.setInterpreter(sim);
      }

      this.getInterpreter().setPredictionMode(saveMode);
   }

   public void setTrace(boolean trace) {
      if (!trace) {
         this.removeParseListener(this._tracer);
         this._tracer = null;
      } else {
         if (this._tracer != null) {
            this.removeParseListener(this._tracer);
         } else {
            this._tracer = new Parser.TraceListener();
         }

         this.addParseListener(this._tracer);
      }
   }

   public boolean isTrace() {
      return this._tracer != null;
   }

   public class TraceListener implements ParseTreeListener {
      @Override
      public void enterEveryRule(ParserRuleContext ctx) {
         System.out.println("enter   " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
      }

      @Override
      public void visitTerminal(TerminalNode node) {
         System.out.println("consume " + node.getSymbol() + " rule " + Parser.this.getRuleNames()[Parser.this._ctx.getRuleIndex()]);
      }

      @Override
      public void visitErrorNode(ErrorNode node) {
      }

      @Override
      public void exitEveryRule(ParserRuleContext ctx) {
         System.out.println("exit    " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
      }
   }

   public static class TrimToSizeListener implements ParseTreeListener {
      public static final Parser.TrimToSizeListener INSTANCE = new Parser.TrimToSizeListener();

      @Override
      public void enterEveryRule(ParserRuleContext ctx) {
      }

      @Override
      public void visitTerminal(TerminalNode node) {
      }

      @Override
      public void visitErrorNode(ErrorNode node) {
      }

      @Override
      public void exitEveryRule(ParserRuleContext ctx) {
         if (ctx.children instanceof ArrayList) {
            ((ArrayList)ctx.children).trimToSize();
         }
      }
   }
}
