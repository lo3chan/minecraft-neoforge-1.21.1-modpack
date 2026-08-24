package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.UnexpectedElementException;
import java.util.Arrays;

public class UnexpectedCharacterException extends ParseException {
   private final Character unexpected;
   private final int position;
   private final VersionParser.CharType[] expected;

   UnexpectedCharacterException(UnexpectedElementException cause) {
      this.position = cause.getPosition();
      this.unexpected = (Character)cause.getUnexpectedElement();
      this.expected = (VersionParser.CharType[])cause.getExpectedElementTypes();
   }

   UnexpectedCharacterException(Character unexpected, int position, VersionParser.CharType... expected) {
      this.unexpected = unexpected;
      this.position = position;
      this.expected = expected;
   }

   Character getUnexpectedCharacter() {
      return this.unexpected;
   }

   int getPosition() {
      return this.position;
   }

   VersionParser.CharType[] getExpectedCharTypes() {
      return this.expected;
   }

   @Override
   public String toString() {
      String message = String.format(
         "Unexpected character '%s(%s)' at position '%d'", VersionParser.CharType.forCharacter(this.unexpected), this.unexpected, this.position
      );
      if (this.expected.length > 0) {
         message = message + String.format(", expecting '%s'", Arrays.toString((Object[])this.expected));
      }

      return message;
   }
}
