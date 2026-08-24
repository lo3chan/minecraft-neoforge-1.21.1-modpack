package amp_libs.org.antlr.v4.runtime.tree;

import amp_libs.org.antlr.v4.runtime.Parser;
import amp_libs.org.antlr.v4.runtime.RuleContext;
import amp_libs.org.antlr.v4.runtime.Token;
import amp_libs.org.antlr.v4.runtime.misc.Interval;

public class TerminalNodeImpl implements TerminalNode {
   public Token symbol;
   public ParseTree parent;

   public TerminalNodeImpl(Token symbol) {
      this.symbol = symbol;
   }

   @Override
   public ParseTree getChild(int i) {
      return null;
   }

   @Override
   public Token getSymbol() {
      return this.symbol;
   }

   @Override
   public ParseTree getParent() {
      return this.parent;
   }

   @Override
   public void setParent(RuleContext parent) {
      this.parent = parent;
   }

   public Token getPayload() {
      return this.symbol;
   }

   @Override
   public Interval getSourceInterval() {
      if (this.symbol == null) {
         return Interval.INVALID;
      } else {
         int tokenIndex = this.symbol.getTokenIndex();
         return new Interval(tokenIndex, tokenIndex);
      }
   }

   @Override
   public int getChildCount() {
      return 0;
   }

   @Override
   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      return (T)visitor.visitTerminal(this);
   }

   @Override
   public String getText() {
      return this.symbol.getText();
   }

   @Override
   public String toStringTree(Parser parser) {
      return this.toString();
   }

   @Override
   public String toString() {
      return this.symbol.getType() == -1 ? "<EOF>" : this.symbol.getText();
   }

   @Override
   public String toStringTree() {
      return this.toString();
   }
}
