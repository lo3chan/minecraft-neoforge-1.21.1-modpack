package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.Stream;
import de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util.UnexpectedElementException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

class VersionParser implements Parser<Version> {
   private final Stream<Character> chars;

   VersionParser(String input) {
      if (input != null && !input.isEmpty()) {
         Character[] elements = new Character[input.length()];

         for (int i = 0; i < input.length(); i++) {
            elements[i] = input.charAt(i);
         }

         this.chars = new Stream<>(elements);
      } else {
         throw new IllegalArgumentException("Input string is NULL or empty");
      }
   }

   public Version parse(String input) {
      return this.parseValidSemVer();
   }

   static Version parseValidSemVer(String version) {
      VersionParser parser = new VersionParser(version);
      return parser.parseValidSemVer();
   }

   static NormalVersion parseVersionCore(String versionCore) {
      VersionParser parser = new VersionParser(versionCore);
      return parser.parseVersionCore();
   }

   static MetadataVersion parsePreRelease(String preRelease) {
      VersionParser parser = new VersionParser(preRelease);
      return parser.parsePreRelease();
   }

   static MetadataVersion parseBuild(String build) {
      VersionParser parser = new VersionParser(build);
      return parser.parseBuild();
   }

   private Version parseValidSemVer() {
      NormalVersion normal = this.parseVersionCore();
      MetadataVersion preRelease = MetadataVersion.NULL;
      MetadataVersion build = MetadataVersion.NULL;
      Character next = this.consumeNextCharacter(VersionParser.CharType.HYPHEN, VersionParser.CharType.PLUS, VersionParser.CharType.EOI);
      if (VersionParser.CharType.HYPHEN.isMatchedBy(next)) {
         preRelease = this.parsePreRelease();
         next = this.consumeNextCharacter(VersionParser.CharType.PLUS, VersionParser.CharType.EOI);
         if (VersionParser.CharType.PLUS.isMatchedBy(next)) {
            build = this.parseBuild();
         }
      } else if (VersionParser.CharType.PLUS.isMatchedBy(next)) {
         build = this.parseBuild();
      }

      this.consumeNextCharacter(VersionParser.CharType.EOI);
      return new Version(normal, preRelease, build);
   }

   private NormalVersion parseVersionCore() {
      int major = Integer.parseInt(this.numericIdentifier());
      this.consumeNextCharacter(VersionParser.CharType.DOT);
      int minor = Integer.parseInt(this.numericIdentifier());
      this.consumeNextCharacter(VersionParser.CharType.DOT);
      int patch = Integer.parseInt(this.numericIdentifier());
      return new NormalVersion(major, minor, patch);
   }

   private MetadataVersion parsePreRelease() {
      this.ensureValidLookahead(VersionParser.CharType.DIGIT, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN);
      List<String> idents = new ArrayList<>();

      while (true) {
         idents.add(this.preReleaseIdentifier());
         if (!this.chars.positiveLookahead(VersionParser.CharType.DOT)) {
            return new MetadataVersion(idents.toArray(new String[idents.size()]));
         }

         this.consumeNextCharacter(VersionParser.CharType.DOT);
      }
   }

   private String preReleaseIdentifier() {
      this.checkForEmptyIdentifier();
      VersionParser.CharType boundary = this.nearestCharType(VersionParser.CharType.DOT, VersionParser.CharType.PLUS, VersionParser.CharType.EOI);
      return this.chars.positiveLookaheadBefore(boundary, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN)
         ? this.alphanumericIdentifier()
         : this.numericIdentifier();
   }

   private MetadataVersion parseBuild() {
      this.ensureValidLookahead(VersionParser.CharType.DIGIT, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN);
      List<String> idents = new ArrayList<>();

      while (true) {
         idents.add(this.buildIdentifier());
         if (!this.chars.positiveLookahead(VersionParser.CharType.DOT)) {
            return new MetadataVersion(idents.toArray(new String[idents.size()]));
         }

         this.consumeNextCharacter(VersionParser.CharType.DOT);
      }
   }

   private String buildIdentifier() {
      this.checkForEmptyIdentifier();
      VersionParser.CharType boundary = this.nearestCharType(VersionParser.CharType.DOT, VersionParser.CharType.EOI);
      return this.chars.positiveLookaheadBefore(boundary, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN)
         ? this.alphanumericIdentifier()
         : this.digits();
   }

   private String numericIdentifier() {
      this.checkForLeadingZeroes();
      return this.digits();
   }

   private String alphanumericIdentifier() {
      StringBuilder sb = new StringBuilder();

      do {
         sb.append(this.consumeNextCharacter(VersionParser.CharType.DIGIT, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN));
      } while (this.chars.positiveLookahead(VersionParser.CharType.DIGIT, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN));

      return sb.toString();
   }

   private String digits() {
      StringBuilder sb = new StringBuilder();

      do {
         sb.append(this.consumeNextCharacter(VersionParser.CharType.DIGIT));
      } while (this.chars.positiveLookahead(VersionParser.CharType.DIGIT));

      return sb.toString();
   }

   private VersionParser.CharType nearestCharType(VersionParser.CharType... types) {
      for (Character chr : this.chars) {
         for (VersionParser.CharType type : types) {
            if (type.isMatchedBy(chr)) {
               return type;
            }
         }
      }

      return VersionParser.CharType.EOI;
   }

   private void checkForLeadingZeroes() {
      Character la1 = this.chars.lookahead(1);
      Character la2 = this.chars.lookahead(2);
      if (la1 != null && la1 == '0' && VersionParser.CharType.DIGIT.isMatchedBy(la2)) {
         throw new ParseException("Numeric identifier MUST NOT contain leading zeroes");
      }
   }

   private void checkForEmptyIdentifier() {
      Character la = this.chars.lookahead(1);
      if (VersionParser.CharType.DOT.isMatchedBy(la) || VersionParser.CharType.PLUS.isMatchedBy(la) || VersionParser.CharType.EOI.isMatchedBy(la)) {
         throw new ParseException(
            "Identifiers MUST NOT be empty",
            new UnexpectedCharacterException(
               la, this.chars.currentOffset(), VersionParser.CharType.DIGIT, VersionParser.CharType.LETTER, VersionParser.CharType.HYPHEN
            )
         );
      }
   }

   private Character consumeNextCharacter(VersionParser.CharType... expected) {
      try {
         return this.chars.consume(expected);
      } catch (UnexpectedElementException var3) {
         throw new UnexpectedCharacterException(var3);
      }
   }

   private void ensureValidLookahead(VersionParser.CharType... expected) {
      if (!this.chars.positiveLookahead(expected)) {
         throw new UnexpectedCharacterException(this.chars.lookahead(1), this.chars.currentOffset(), expected);
      }
   }

   static enum CharType implements Stream.ElementType<Character> {
      DIGIT {
         public boolean isMatchedBy(Character chr) {
            return chr == null ? false : chr >= '0' && chr <= '9';
         }
      },
      LETTER {
         public boolean isMatchedBy(Character chr) {
            return chr == null ? false : chr >= 'a' && chr <= 'z' || chr >= 'A' && chr <= 'Z';
         }
      },
      DOT {
         public boolean isMatchedBy(Character chr) {
            return chr == null ? false : chr == '.';
         }
      },
      HYPHEN {
         public boolean isMatchedBy(Character chr) {
            return chr == null ? false : chr == '-';
         }
      },
      PLUS {
         public boolean isMatchedBy(Character chr) {
            return chr == null ? false : chr == '+';
         }
      },
      EOI {
         public boolean isMatchedBy(Character chr) {
            return chr == null;
         }
      },
      ILLEGAL {
         public boolean isMatchedBy(Character chr) {
            EnumSet<VersionParser.CharType> itself = EnumSet.of(ILLEGAL);

            for (VersionParser.CharType type : EnumSet.complementOf(itself)) {
               if (type.isMatchedBy(chr)) {
                  return false;
               }
            }

            return true;
         }
      };

      private CharType() {
      }

      static VersionParser.CharType forCharacter(Character chr) {
         for (VersionParser.CharType type : values()) {
            if (type.isMatchedBy(chr)) {
               return type;
            }
         }

         return null;
      }
   }
}
