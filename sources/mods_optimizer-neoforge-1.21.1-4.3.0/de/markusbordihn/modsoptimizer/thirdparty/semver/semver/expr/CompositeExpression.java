package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;

public class CompositeExpression implements Expression {
   private Expression exprTree;

   public CompositeExpression(Expression expr) {
      this.exprTree = expr;
   }

   public CompositeExpression and(Expression expr) {
      this.exprTree = new And(this.exprTree, expr);
      return this;
   }

   public CompositeExpression or(Expression expr) {
      this.exprTree = new Or(this.exprTree, expr);
      return this;
   }

   public boolean interpret(String version) {
      return this.interpret(Version.valueOf(version));
   }

   @Override
   public boolean interpret(Version version) {
      return this.exprTree.interpret(version);
   }

   public static class Helper {
      public static CompositeExpression not(Expression expr) {
         return new CompositeExpression(new Not(expr));
      }

      public static CompositeExpression eq(Version version) {
         return new CompositeExpression(new Equal(version));
      }

      public static CompositeExpression eq(String version) {
         return eq(Version.valueOf(version));
      }

      public static CompositeExpression neq(Version version) {
         return new CompositeExpression(new NotEqual(version));
      }

      public static CompositeExpression neq(String version) {
         return neq(Version.valueOf(version));
      }

      public static CompositeExpression gt(Version version) {
         return new CompositeExpression(new Greater(version));
      }

      public static CompositeExpression gt(String version) {
         return gt(Version.valueOf(version));
      }

      public static CompositeExpression gte(Version version) {
         return new CompositeExpression(new GreaterOrEqual(version));
      }

      public static CompositeExpression gte(String version) {
         return gte(Version.valueOf(version));
      }

      public static CompositeExpression lt(Version version) {
         return new CompositeExpression(new Less(version));
      }

      public static CompositeExpression lt(String version) {
         return lt(Version.valueOf(version));
      }

      public static CompositeExpression lte(Version version) {
         return new CompositeExpression(new LessOrEqual(version));
      }

      public static CompositeExpression lte(String version) {
         return lte(Version.valueOf(version));
      }
   }
}
