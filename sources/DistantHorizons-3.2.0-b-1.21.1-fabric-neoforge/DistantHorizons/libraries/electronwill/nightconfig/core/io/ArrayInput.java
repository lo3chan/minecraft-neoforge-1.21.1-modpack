package DistantHorizons.libraries.electronwill.nightconfig.core.io;

public final class ArrayInput extends AbstractInput {
   private final char[] chars;
   private final int limit;
   private int cursor;

   public ArrayInput(CharsWrapper chars) {
      this(chars.chars, chars.offset, chars.limit);
   }

   public ArrayInput(char[] chars) {
      this(chars, 0, chars.length);
   }

   public ArrayInput(char[] chars, int offset, int limit) {
      this.chars = chars;
      this.cursor = offset;
      this.limit = limit;
   }

   @Override
   protected int directRead() {
      return this.cursor >= this.limit ? -1 : this.chars[this.cursor++];
   }

   @Override
   protected char directReadChar() throws ParsingException {
      if (this.cursor >= this.limit) {
         throw ParsingException.notEnoughData();
      } else {
         return this.chars[this.cursor++];
      }
   }

   @Override
   public CharsWrapper read(int n) {
      int size = Math.min(n, this.limit - this.cursor + this.deque.size());
      int offset = Math.min(this.deque.size(), size);
      char[] array = new char[size];
      CharsWrapper smaller = this.consumeDeque(array, offset, false);
      if (smaller != null) {
         return smaller;
      } else {
         System.arraycopy(this.chars, this.cursor, array, offset, size - offset);
         this.cursor += size;
         return new CharsWrapper(array);
      }
   }

   @Override
   public CharsWrapper readChars(int n) {
      if (this.limit - this.cursor + this.deque.size() < n) {
         throw ParsingException.notEnoughData();
      } else {
         int offset = Math.min(this.deque.size(), n);
         char[] array = new char[n];
         this.consumeDeque(array, offset, true);
         System.arraycopy(this.chars, this.cursor, array, offset, n - offset);
         this.cursor += n;
         return new CharsWrapper(array);
      }
   }
}
