package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;

class Or implements Expression {
   private final Expression left;
   private final Expression right;

   Or(Expression left, Expression right) {
      this.left = left;
      this.right = right;
   }

   @Override
   public boolean interpret(Version version) {
      return this.left.interpret(version) || this.right.interpret(version);
   }
}
