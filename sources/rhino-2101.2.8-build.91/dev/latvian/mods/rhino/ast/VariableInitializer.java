package dev.latvian.mods.rhino.ast;

public class VariableInitializer extends AstNode {
   private AstNode target;
   private AstNode initializer;

   public VariableInitializer() {
      this.type = 124;
   }

   public VariableInitializer(int pos) {
      super(pos);
      this.type = 124;
   }

   public VariableInitializer(int pos, int len) {
      super(pos, len);
      this.type = 124;
   }

   public void setNodeType(int nodeType) {
      if (nodeType != 124 && nodeType != 156 && nodeType != 155) {
         throw new IllegalArgumentException("invalid node type");
      } else {
         this.setType(nodeType);
      }
   }

   public boolean isDestructuring() {
      return !(this.target instanceof Name);
   }

   public AstNode getTarget() {
      return this.target;
   }

   public void setTarget(AstNode target) {
      if (target == null) {
         throw new IllegalArgumentException("invalid target arg");
      } else {
         this.target = target;
         target.setParent(this);
      }
   }

   public AstNode getInitializer() {
      return this.initializer;
   }

   public void setInitializer(AstNode initializer) {
      this.initializer = initializer;
      if (initializer != null) {
         initializer.setParent(this);
      }
   }
}
