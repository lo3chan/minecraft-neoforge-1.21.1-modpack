package pl.skidam.automodpack_core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SemanticVersion(int major, int minor, int patch, String label, int preVersion) implements Comparable<SemanticVersion> {
   private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(?:-(.+))?$");
   private static final Pattern PRE_SPLIT_PATTERN = Pattern.compile("^([a-zA-Z]+)(?:[.\\-]?)(\\d+)?$");

   public static SemanticVersion parse(String versionString) {
      if (versionString != null && !versionString.isBlank()) {
         Matcher matcher = VERSION_PATTERN.matcher(versionString);
         if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid version format: " + versionString);
         } else {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));
            String rawPre = matcher.group(4);
            if (rawPre == null) {
               return new SemanticVersion(major, minor, patch, "release", 2147483647);
            } else {
               Matcher preMatcher = PRE_SPLIT_PATTERN.matcher(rawPre);
               String label = rawPre;
               int preVer = 1;
               if (preMatcher.find()) {
                  label = preMatcher.group(1).toLowerCase();
                  String numPart = preMatcher.group(2);
                  if (numPart != null) {
                     preVer = Integer.parseInt(numPart);
                  }
               }

               return new SemanticVersion(major, minor, patch, label, preVer);
            }
         }
      } else {
         throw new IllegalArgumentException("Version cannot be empty");
      }
   }

   public boolean isStable() {
      return "release".equals(this.label);
   }

   private int getLabelWeight() {
      String var1 = this.label;

      return switch (var1) {
         case "release" -> 100;
         case "rc", "pre" -> 50;
         case "beta" -> 30;
         case "alpha" -> 10;
         case "snapshot" -> 5;
         default -> 0;
      };
   }

   public int compareTo(SemanticVersion o) {
      if (this.major != o.major) {
         return Integer.compare(this.major, o.major);
      } else if (this.minor != o.minor) {
         return Integer.compare(this.minor, o.minor);
      } else if (this.patch != o.patch) {
         return Integer.compare(this.patch, o.patch);
      } else {
         int thisWeight = this.getLabelWeight();
         int otherWeight = o.getLabelWeight();
         return thisWeight != otherWeight ? Integer.compare(thisWeight, otherWeight) : Integer.compare(this.preVersion, o.preVersion);
      }
   }

   @Override
   public String toString() {
      return this.isStable()
         ? String.format("%d.%d.%d", this.major, this.minor, this.patch)
         : String.format("%d.%d.%d-%s.%d", this.major, this.minor, this.patch, this.label, this.preVersion);
   }
}
