package net.sourceforge.jaad.mp4.boxes;

public final class Utils {
   private static final long UNDETERMINED = 4294967295L;

   public static String getLanguageCode(long l) {
      char[] c = new char[]{(char)((l >> 10 & 31L) + 96L), (char)((l >> 5 & 31L) + 96L), (char)((l & 31L) + 96L)};
      return new String(c);
   }

   public static long detectUndetermined(long l) {
      long x;
      if (l == 4294967295L) {
         x = -1L;
      } else {
         x = l;
      }

      return x;
   }
}
