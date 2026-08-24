package dev.latvian.mods.rhino.ast;

import dev.latvian.mods.rhino.Token;

public class UnaryExpression extends AstNode {
   private AstNode operand;

   public UnaryExpression() {
   }

   public UnaryExpression(int pos) {
      super(pos);
   }

   public UnaryExpression(int pos, int len) {
      super(pos, len);
   }

   public UnaryExpression(int operator, int operatorPosition, AstNode operand) {
      this.assertNotNull(operand);
      int beg = operand.getPosition();
      int end = operand.getPosition() + operand.getLength();
      this.setBounds(beg, end);
      this.setOperator(operator);
      this.setOperand(operand);
   }

   public int getOperator() {
      return this.type;
   }

   public void setOperator(int operator) {
      if (!Token.isValidToken(operator)) {
         throw new IllegalArgumentException("Invalid token: " + operator);
      } else {
         this.setType(operator);
      }
   }

   public AstNode getOperand() {
      return this.operand;
   }

   public void setOperand(AstNode operand) {
      this.assertNotNull(operand);
      this.operand = operand;
      operand.setParent(this);
   }
}
