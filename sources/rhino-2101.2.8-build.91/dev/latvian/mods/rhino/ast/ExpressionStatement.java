package dev.latvian.mods.rhino.ast;

public class ExpressionStatement extends AstNode {
   private AstNode expr;

   public ExpressionStatement() {
      this.type = 135;
   }

   public ExpressionStatement(AstNode expr, boolean hasResult) {
      this(expr);
      if (hasResult) {
         this.setHasResult();
      }
   }

   public ExpressionStatement(AstNode expr) {
      this(expr.getPosition(), expr.getLength(), expr);
   }

   public ExpressionStatement(int pos, int len) {
      super(pos, len);
      this.type = 135;
   }

   public ExpressionStatement(int pos, int len, AstNode expr) {
      super(pos, len);
      this.type = 135;
      this.setExpression(expr);
   }

   public void setHasResult() {
      this.type = 136;
   }

   public AstNode getExpression() {
      return this.expr;
   }

   public void setExpression(AstNode expression) {
      this.assertNotNull(expression);
      this.expr = expression;
      expression.setParent(this);
      this.setLineColumnNumber(expression.getLineno(), this.expr.getColumn());
   }

   @Override
   public boolean hasSideEffects() {
      return this.type == 136 || this.expr.hasSideEffects();
   }
}
