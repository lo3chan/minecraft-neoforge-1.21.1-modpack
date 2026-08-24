package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;

class Not implements Expression {
   private final Expression expr;

   Not(Expression expr) {
      this.expr = expr;
   }

   @Override
   public boolean interpret(Version version) {
      return !this.expr.interpret(version);
   }
}
