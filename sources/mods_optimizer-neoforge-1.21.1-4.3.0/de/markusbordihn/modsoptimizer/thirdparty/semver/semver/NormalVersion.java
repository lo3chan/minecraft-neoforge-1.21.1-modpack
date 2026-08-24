package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

class NormalVersion implements Comparable<NormalVersion> {
   private final int major;
   private final int minor;
   private final int patch;

   NormalVersion(int major, int minor, int patch) {
      if (major >= 0 && minor >= 0 && patch >= 0) {
         this.major = major;
         this.minor = minor;
         this.patch = patch;
      } else {
         throw new IllegalArgumentException("Major, minor and patch versions MUST be non-negative integers.");
      }
   }

   int getMajor() {
      return this.major;
   }

   int getMinor() {
      return this.minor;
   }

   int getPatch() {
      return this.patch;
   }

   NormalVersion incrementMajor() {
      return new NormalVersion(this.major + 1, 0, 0);
   }

   NormalVersion incrementMinor() {
      return new NormalVersion(this.major, this.minor + 1, 0);
   }

   NormalVersion incrementPatch() {
      return new NormalVersion(this.major, this.minor, this.patch + 1);
   }

   public int compareTo(NormalVersion other) {
      int result = this.major - other.major;
      if (result == 0) {
         result = this.minor - other.minor;
         if (result == 0) {
            result = this.patch - other.patch;
         }
      }

      return result;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof NormalVersion) ? false : this.compareTo((NormalVersion)other) == 0;
      }
   }

   @Override
   public int hashCode() {
      int hash = 17;
      hash = 31 * hash + this.major;
      hash = 31 * hash + this.minor;
      return 31 * hash + this.patch;
   }

   @Override
   public String toString() {
      return String.format("%d.%d.%d", this.major, this.minor, this.patch);
   }
}
