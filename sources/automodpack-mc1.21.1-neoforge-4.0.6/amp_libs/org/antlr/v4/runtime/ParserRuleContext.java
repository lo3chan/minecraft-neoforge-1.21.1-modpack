package amp_libs.org.antlr.v4.runtime;

import amp_libs.org.antlr.v4.runtime.misc.Interval;
import amp_libs.org.antlr.v4.runtime.tree.ErrorNode;
import amp_libs.org.antlr.v4.runtime.tree.ErrorNodeImpl;
import amp_libs.org.antlr.v4.runtime.tree.ParseTree;
import amp_libs.org.antlr.v4.runtime.tree.ParseTreeListener;
import amp_libs.org.antlr.v4.runtime.tree.TerminalNode;
import amp_libs.org.antlr.v4.runtime.tree.TerminalNodeImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParserRuleContext extends RuleContext {
   public static final ParserRuleContext EMPTY = new ParserRuleContext();
   public List<ParseTree> children;
   public Token start;
   public Token stop;
   public RecognitionException exception;

   public ParserRuleContext() {
   }

   public void copyFrom(ParserRuleContext ctx) {
      this.parent = ctx.parent;
      this.invokingState = ctx.invokingState;
      this.start = ctx.start;
      this.stop = ctx.stop;
      if (ctx.children != null) {
         this.children = new ArrayList<>();

         for (ParseTree child : ctx.children) {
            if (child instanceof ErrorNode) {
               this.addChild((ErrorNode)child);
            }
         }
      }
   }

   public ParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
      super(parent, invokingStateNumber);
   }

   public void enterRule(ParseTreeListener listener) {
   }

   public void exitRule(ParseTreeListener listener) {
   }

   public <T extends ParseTree> T addAnyChild(T t) {
      if (this.children == null) {
         this.children = new ArrayList<>();
      }

      this.children.add(t);
      return t;
   }

   public RuleContext addChild(RuleContext ruleInvocation) {
      return this.addAnyChild(ruleInvocation);
   }

   public TerminalNode addChild(TerminalNode t) {
      t.setParent(this);
      return this.addAnyChild(t);
   }

   public ErrorNode addErrorNode(ErrorNode errorNode) {
      errorNode.setParent(this);
      return this.addAnyChild(errorNode);
   }

   @Deprecated
   public TerminalNode addChild(Token matchedToken) {
      TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
      this.addAnyChild(t);
      t.setParent(this);
      return t;
   }

   @Deprecated
   public ErrorNode addErrorNode(Token badToken) {
      ErrorNodeImpl t = new ErrorNodeImpl(badToken);
      this.addAnyChild(t);
      t.setParent(this);
      return t;
   }

   public void removeLastChild() {
      if (this.children != null) {
         this.children.remove(this.children.size() - 1);
      }
   }

   public ParserRuleContext getParent() {
      return (ParserRuleContext)super.getParent();
   }

   @Override
   public ParseTree getChild(int i) {
      return this.children != null && i >= 0 && i < this.children.size() ? this.children.get(i) : null;
   }

   public <T extends ParseTree> T getChild(Class<? extends T> ctxType, int i) {
      if (this.children != null && i >= 0 && i < this.children.size()) {
         int j = -1;

         for (ParseTree o : this.children) {
            if (ctxType.isInstance(o)) {
               if (++j == i) {
                  return (T)ctxType.cast(o);
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public TerminalNode getToken(int ttype, int i) {
      if (this.children != null && i >= 0 && i < this.children.size()) {
         int j = -1;

         for (ParseTree o : this.children) {
            if (o instanceof TerminalNode) {
               TerminalNode tnode = (TerminalNode)o;
               Token symbol = tnode.getSymbol();
               if (symbol.getType() == ttype) {
                  if (++j == i) {
                     return tnode;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public List<TerminalNode> getTokens(int ttype) {
      if (this.children == null) {
         return Collections.emptyList();
      } else {
         List<TerminalNode> tokens = null;

         for (ParseTree o : this.children) {
            if (o instanceof TerminalNode) {
               TerminalNode tnode = (TerminalNode)o;
               Token symbol = tnode.getSymbol();
               if (symbol.getType() == ttype) {
                  if (tokens == null) {
                     tokens = new ArrayList<>();
                  }

                  tokens.add(tnode);
               }
            }
         }

         return tokens == null ? Collections.emptyList() : tokens;
      }
   }

   public <T extends ParserRuleContext> T getRuleContext(Class<? extends T> ctxType, int i) {
      return this.getChild(ctxType, i);
   }

   public <T extends ParserRuleContext> List<T> getRuleContexts(Class<? extends T> ctxType) {
      if (this.children == null) {
         return Collections.emptyList();
      } else {
         List<T> contexts = null;

         for (ParseTree o : this.children) {
            if (ctxType.isInstance(o)) {
               if (contexts == null) {
                  contexts = new ArrayList<>();
               }

               contexts.add((T)ctxType.cast(o));
            }
         }

         return contexts == null ? Collections.emptyList() : contexts;
      }
   }

   @Override
   public int getChildCount() {
      return this.children != null ? this.children.size() : 0;
   }

   @Override
   public Interval getSourceInterval() {
      if (this.start == null) {
         return Interval.INVALID;
      } else {
         return this.stop != null && this.stop.getTokenIndex() >= this.start.getTokenIndex()
            ? Interval.of(this.start.getTokenIndex(), this.stop.getTokenIndex())
            : Interval.of(this.start.getTokenIndex(), this.start.getTokenIndex() - 1);
      }
   }

   public Token getStart() {
      return this.start;
   }

   public Token getStop() {
      return this.stop;
   }

   public String toInfoString(Parser recognizer) {
      List<String> rules = recognizer.getRuleInvocationStack(this);
      Collections.reverse(rules);
      return "ParserRuleContext" + rules + "{start=" + this.start + ", stop=" + this.stop + '}';
   }
}
