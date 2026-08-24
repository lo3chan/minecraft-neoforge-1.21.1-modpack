package dev.latvian.mods.rhino.ast;

public class GeneratorMethodDefinition extends AstNode {
   private AstNode methodName;

   public GeneratorMethodDefinition(int pos, int len, AstNode methodName) {
      super(pos, len);
      this.setType(23);
      this.setMethodName(methodName);
   }

   public AstNode getMethodName() {
      return this.methodName;
   }

   public void setMethodName(AstNode methodName) {
      this.assertNotNull(methodName);
      this.methodName = methodName;
      methodName.setParent(this);
   }

   @Override
   public boolean hasSideEffects() {
      if (this.methodName == null) {
         codeBug();
      }

      return this.methodName.hasSideEffects();
   }
}
