package amp_libs.org.antlr.v4.runtime.tree;

import amp_libs.org.antlr.v4.runtime.Token;

public class ErrorNodeImpl extends TerminalNodeImpl implements ErrorNode {
   public ErrorNodeImpl(Token token) {
      super(token);
   }

   @Override
   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      return (T)visitor.visitErrorNode(this);
   }
}
