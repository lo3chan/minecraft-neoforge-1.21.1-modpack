package dev.latvian.mods.rhino.ast;

public class EmptyStatement extends AstNode {
   public EmptyStatement() {
      this.type = 130;
   }

   public EmptyStatement(int pos) {
      super(pos);
      this.type = 130;
   }

   public EmptyStatement(int pos, int len) {
      super(pos, len);
      this.type = 130;
   }
}
