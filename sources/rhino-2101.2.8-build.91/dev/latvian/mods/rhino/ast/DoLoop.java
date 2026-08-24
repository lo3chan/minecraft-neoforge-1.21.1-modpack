package dev.latvian.mods.rhino.ast;

public class DoLoop extends Loop {
   private AstNode condition;
   private int whilePosition = -1;

   public DoLoop() {
      this.type = 120;
   }

   public DoLoop(int pos) {
      super(pos);
      this.type = 120;
   }

   public DoLoop(int pos, int len) {
      super(pos, len);
      this.type = 120;
   }

   public AstNode getCondition() {
      return this.condition;
   }

   public void setCondition(AstNode condition) {
      this.assertNotNull(condition);
      this.condition = condition;
      condition.setParent(this);
   }

   public int getWhilePosition() {
      return this.whilePosition;
   }

   public void setWhilePosition(int whilePosition) {
      this.whilePosition = whilePosition;
   }
}
