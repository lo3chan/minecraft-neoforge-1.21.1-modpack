package dev.latvian.mods.rhino.ast;

public class TaggedTemplateLiteral extends AstNode {
   private AstNode target;
   private AstNode templateLiteral;

   public TaggedTemplateLiteral() {
      this.type = 171;
   }

   public TaggedTemplateLiteral(int pos) {
      super(pos);
      this.type = 171;
   }

   public TaggedTemplateLiteral(int pos, int len) {
      super(pos, len);
      this.type = 171;
   }

   public AstNode getTarget() {
      return this.target;
   }

   public void setTarget(AstNode target) {
      this.target = target;
      target.setParent(this);
   }

   public AstNode getTemplateLiteral() {
      return this.templateLiteral;
   }

   public void setTemplateLiteral(AstNode templateLiteral) {
      this.templateLiteral = templateLiteral;
      templateLiteral.setParent(this);
   }
}
