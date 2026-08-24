package DistantHorizons.libraries.electronwill.nightconfig.core.io;

public interface CharacterOutput {
   void write(char c);

   default void write(char... chars) {
      this.write(chars, 0, chars.length);
   }

   void write(char[] cs, int i, int j);

   default void write(String s) {
      this.write(s, 0, s.length());
   }

   void write(String string, int i, int j);

   default void write(CharsWrapper cw) {
      this.write(cw.chars, cw.offset, cw.limit - cw.offset);
   }
}
