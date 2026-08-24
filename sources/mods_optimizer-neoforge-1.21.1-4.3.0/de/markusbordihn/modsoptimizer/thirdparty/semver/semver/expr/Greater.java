package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;

class Greater implements Expression {
   private final Version parsedVersion;

   Greater(Version parsedVersion) {
      this.parsedVersion = parsedVersion;
   }

   @Override
   public boolean interpret(Version version) {
      return version.greaterThan(this.parsedVersion);
   }
}
