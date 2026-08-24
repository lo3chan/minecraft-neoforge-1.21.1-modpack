package net.blay09.mods.balm.common.proxy;

import java.util.ArrayList;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public class VersionRange {
   @Nullable
   private final String lower;
   private final boolean lowerInclusive;
   @Nullable
   private final String upper;
   private final boolean upperInclusive;

   private VersionRange(@Nullable String lower, boolean lowerInclusive, @Nullable String upper, boolean upperInclusive) {
      this.lower = lower;
      this.lowerInclusive = lowerInclusive;
      this.upper = upper;
      this.upperInclusive = upperInclusive;
   }

   public boolean contains(String version) {
      if (this.lower != null) {
         int lowerComparison = compareVersions(version, this.lower);
         if (lowerComparison < 0 || lowerComparison == 0 && !this.lowerInclusive) {
            return false;
         }
      }

      if (this.upper == null) {
         return true;
      } else {
         int upperComparison = compareVersions(version, this.upper);
         return upperComparison <= 0 && (upperComparison != 0 || this.upperInclusive);
      }
   }

   public static VersionRange parse(String versionRange) {
      if (versionRange.isEmpty()) {
         throw new IllegalArgumentException("Invalid version range '" + versionRange + "'");
      } else {
         boolean lowerInclusive = switch (versionRange.charAt(0)) {
            case '(' -> false;
            case '[' -> true;
            default -> throw new IllegalArgumentException("Invalid version range '" + versionRange + "'");
         };
         int closingSquareIndex = versionRange.lastIndexOf(93);
         int closingParenIndex = versionRange.lastIndexOf(41);
         int endIndex = Math.max(closingSquareIndex, closingParenIndex);
         if (endIndex != -1 && endIndex == versionRange.length() - 1) {
            char endChar = versionRange.charAt(endIndex);
            String range = versionRange.substring(1, endIndex);
            int separatorIndex = range.indexOf(44);
            if (separatorIndex != range.lastIndexOf(44)) {
               throw new IllegalArgumentException("Invalid version range '" + versionRange + "'");
            } else if (separatorIndex == -1) {
               String version = range.trim();
               if (version.isEmpty()) {
                  throw new IllegalArgumentException("Invalid version range '" + versionRange + "'");
               } else {
                  return new VersionRange(version, lowerInclusive, version, endChar == ']');
               }
            } else {
               String lower = range.substring(0, separatorIndex).trim();
               String upper = range.substring(separatorIndex + 1).trim();
               return new VersionRange(lower.isEmpty() ? null : lower, lowerInclusive, upper.isEmpty() ? null : upper, endChar == ']');
            }
         } else {
            throw new IllegalArgumentException("Invalid version range '" + versionRange + "'");
         }
      }
   }

   private static int compareVersions(String left, String right) {
      ArrayList<VersionRange.VersionPart> leftParts = tokenize(stripBuildMetadata(left));
      ArrayList<VersionRange.VersionPart> rightParts = tokenize(stripBuildMetadata(right));
      int maxLength = Math.max(leftParts.size(), rightParts.size());

      for (int i = 0; i < maxLength; i++) {
         VersionRange.VersionPart leftPart = i < leftParts.size() ? leftParts.get(i) : VersionRange.VersionPart.ZERO;
         VersionRange.VersionPart rightPart = i < rightParts.size() ? rightParts.get(i) : VersionRange.VersionPart.ZERO;
         int comparison = leftPart.compareTo(rightPart);
         if (comparison != 0) {
            return comparison;
         }
      }

      return 0;
   }

   private static String stripBuildMetadata(String version) {
      int buildMetadataIndex = version.indexOf(43);
      return buildMetadataIndex != -1 ? version.substring(0, buildMetadataIndex) : version;
   }

   private static ArrayList<VersionRange.VersionPart> tokenize(String version) {
      ArrayList<VersionRange.VersionPart> parts = new ArrayList<>();
      StringBuilder token = new StringBuilder();
      Boolean digitToken = null;

      for (int i = 0; i < version.length(); i++) {
         char character = version.charAt(i);
         boolean isDigit = Character.isDigit(character);
         if (!Character.isLetterOrDigit(character)) {
            flushToken(parts, token, digitToken);
            digitToken = null;
         } else {
            if (digitToken != null && digitToken != isDigit) {
               flushToken(parts, token, digitToken);
            }

            token.append(character);
            digitToken = isDigit;
         }
      }

      flushToken(parts, token, digitToken);
      return parts;
   }

   private static void flushToken(ArrayList<VersionRange.VersionPart> parts, StringBuilder token, @Nullable Boolean digitToken) {
      if (!token.isEmpty() && digitToken != null) {
         parts.add(digitToken ? VersionRange.VersionPart.numeric(token.toString()) : VersionRange.VersionPart.qualifier(token.toString()));
         token.setLength(0);
      }
   }

   private record VersionPart(long number, String qualifier, boolean numeric) implements Comparable<VersionRange.VersionPart> {
      private static final VersionRange.VersionPart ZERO = numeric("0");

      private static VersionRange.VersionPart numeric(String value) {
         try {
            return new VersionRange.VersionPart(Long.parseLong(value), "", true);
         } catch (NumberFormatException var2) {
            return new VersionRange.VersionPart(9223372036854775807L, value, true);
         }
      }

      private static VersionRange.VersionPart qualifier(String value) {
         return new VersionRange.VersionPart(0L, value.toLowerCase(Locale.ROOT), false);
      }

      public int compareTo(VersionRange.VersionPart other) {
         if (this.numeric != other.numeric) {
            return this.numeric ? 1 : -1;
         } else {
            return this.numeric ? Long.compare(this.number, other.number) : this.qualifier.compareTo(other.qualifier);
         }
      }
   }
}
