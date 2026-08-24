package dev.latvian.mods.rhino.ast;

import dev.latvian.mods.rhino.Node;
import dev.latvian.mods.rhino.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VariableDeclaration extends AstNode {
   private final List<VariableInitializer> variables = new ArrayList<>();
   private boolean isStatement;

   public VariableDeclaration() {
      this.type = 124;
   }

   public VariableDeclaration(int pos) {
      super(pos);
      this.type = 124;
   }

   public VariableDeclaration(int pos, int len) {
      super(pos, len);
      this.type = 124;
   }

   public List<VariableInitializer> getVariables() {
      return this.variables;
   }

   public void setVariables(List<VariableInitializer> variables) {
      this.assertNotNull(variables);
      this.variables.clear();

      for (VariableInitializer vi : variables) {
         this.addVariable(vi);
      }
   }

   public void addVariable(VariableInitializer v) {
      this.assertNotNull(v);
      this.variables.add(v);
      v.setParent(this);
   }

   @Override
   public Node setType(int type) {
      if (type != 124 && type != 156 && type != 155) {
         throw new IllegalArgumentException("invalid decl type: " + type);
      } else {
         return super.setType(type);
      }
   }

   public boolean isVar() {
      return this.type == 124;
   }

   public boolean isConst() {
      return this.type == 156;
   }

   public boolean isLet() {
      return this.type == 155;
   }

   public boolean isStatement() {
      return this.isStatement;
   }

   public void setIsStatement(boolean isStatement) {
      this.isStatement = isStatement;
   }

   private String declTypeName() {
      return Token.typeToName(this.type).toLowerCase(Locale.ROOT);
   }
}
