package de.markusbordihn.modsoptimizer.thirdparty.semver.semver;

import java.util.Arrays;

class MetadataVersion implements Comparable<MetadataVersion> {
   static final MetadataVersion NULL = new MetadataVersion.NullMetadataVersion();
   private final String[] idents;

   MetadataVersion(String[] identifiers) {
      this.idents = identifiers;
   }

   MetadataVersion increment() {
      String[] ids = this.idents;
      String lastId = ids[ids.length - 1];
      if (this.isInt(lastId)) {
         int intId = Integer.parseInt(lastId);
         ids[ids.length - 1] = String.valueOf(++intId);
      } else {
         ids = Arrays.copyOf(ids, ids.length + 1);
         ids[ids.length - 1] = String.valueOf(1);
      }

      return new MetadataVersion(ids);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof MetadataVersion) ? false : this.compareTo((MetadataVersion)other) == 0;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.idents);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      for (String ident : this.idents) {
         sb.append(ident).append(".");
      }

      return sb.deleteCharAt(sb.lastIndexOf(".")).toString();
   }

   public int compareTo(MetadataVersion other) {
      if (other == NULL) {
         return -1;
      } else {
         int result = this.compareIdentifierArrays(other.idents);
         if (result == 0) {
            result = this.idents.length - other.idents.length;
         }

         return result;
      }
   }

   private int compareIdentifierArrays(String[] otherIdents) {
      int result = 0;
      int length = this.getLeastCommonArrayLength(this.idents, otherIdents);

      for (int i = 0; i < length; i++) {
         result = this.compareIdentifiers(this.idents[i], otherIdents[i]);
         if (result != 0) {
            break;
         }
      }

      return result;
   }

   private int getLeastCommonArrayLength(String[] arr1, String[] arr2) {
      return arr1.length <= arr2.length ? arr1.length : arr2.length;
   }

   private int compareIdentifiers(String ident1, String ident2) {
      return this.isInt(ident1) && this.isInt(ident2) ? Integer.parseInt(ident1) - Integer.parseInt(ident2) : ident1.compareTo(ident2);
   }

   private boolean isInt(String str) {
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   private static class NullMetadataVersion extends MetadataVersion {
      public NullMetadataVersion() {
         super(null);
      }

      @Override
      MetadataVersion increment() {
         throw new NullPointerException("Metadata version is NULL");
      }

      @Override
      public String toString() {
         return "";
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      public boolean equals(Object other) {
         return other instanceof MetadataVersion.NullMetadataVersion;
      }

      @Override
      public int compareTo(MetadataVersion other) {
         return !this.equals(other) ? 1 : 0;
      }
   }
}
