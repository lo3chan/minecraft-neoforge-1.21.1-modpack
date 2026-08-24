package dev.latvian.mods.rhino.ast;

public class ComputedPropertyKey extends AstNode {
   private AstNode expression;

   public ComputedPropertyKey(int pos, int len) {
      super(pos, len);
      this.type = 172;
   }

   public AstNode getExpression() {
      return this.expression;
   }

   public void setExpression(AstNode expression) {
      this.assertNotNull(expression);
      this.expression = expression;
      expression.setParent(this);
   }

   @Override
   public boolean hasSideEffects() {
      if (this.expression == null) {
         codeBug();
      }

      return this.expression.hasSideEffects();
   }
}
