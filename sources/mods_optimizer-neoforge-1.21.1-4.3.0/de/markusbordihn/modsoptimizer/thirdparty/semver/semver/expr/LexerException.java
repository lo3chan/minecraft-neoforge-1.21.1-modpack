package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.expr;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.ParseException;

public class LexerException extends ParseException {
   private final String expr;

   LexerException(String expr) {
      this.expr = expr;
   }

   @Override
   public String toString() {
      return "Illegal character near '" + this.expr + "'";
   }
}
