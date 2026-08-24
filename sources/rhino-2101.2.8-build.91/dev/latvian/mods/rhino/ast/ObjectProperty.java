package dev.latvian.mods.rhino.ast;

public class ObjectProperty extends InfixExpression {
   public ObjectProperty() {
      this.type = 105;
   }

   public ObjectProperty(int pos) {
      super(pos);
      this.type = 105;
   }

   public ObjectProperty(int pos, int len) {
      super(pos, len);
      this.type = 105;
   }

   public void setNodeType(int nodeType) {
      if (nodeType != 105 && nodeType != 153 && nodeType != 154 && nodeType != 165) {
         throw new IllegalArgumentException("invalid node type: " + nodeType);
      } else {
         this.setType(nodeType);
      }
   }

   public void setIsGetterMethod() {
      this.type = 153;
   }

   public boolean isGetterMethod() {
      return this.type == 153;
   }

   public void setIsSetterMethod() {
      this.type = 154;
   }

   public boolean isSetterMethod() {
      return this.type == 154;
   }

   public void setIsNormalMethod() {
      this.type = 165;
   }

   public boolean isNormalMethod() {
      return this.type == 165;
   }

   public boolean isMethod() {
      return this.isGetterMethod() || this.isSetterMethod() || this.isNormalMethod();
   }
}
