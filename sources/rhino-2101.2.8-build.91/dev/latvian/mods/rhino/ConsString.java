package dev.latvian.mods.rhino;

public class ConsString {
   public static String flatten(CharSequence left, CharSequence right) {
      int l = left.length();
      int r = right.length();
      char[] chars = new char[l + r];
      left.toString().getChars(0, l, chars, 0);
      right.toString().getChars(0, r, chars, l);
      return new String(chars);
   }
}
