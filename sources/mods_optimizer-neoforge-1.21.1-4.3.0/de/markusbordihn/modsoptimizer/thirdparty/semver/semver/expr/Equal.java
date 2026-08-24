package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.Version;

class Equal implements Expression {
   private final Version parsedVersion;

   Equal(Version parsedVersion) {
      this.parsedVersion = parsedVersion;
   }

   @Override
   public boolean interpret(Version version) {
      return version.equals(this.parsedVersion);
   }
}
