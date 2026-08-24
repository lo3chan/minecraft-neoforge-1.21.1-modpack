package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;

class NotEqual implements Expression {
   private final Version parsedVersion;

   NotEqual(Version parsedVersion) {
      this.parsedVersion = parsedVersion;
   }

   @Override
   public boolean interpret(Version version) {
      return !version.equals(this.parsedVersion);
   }
}
