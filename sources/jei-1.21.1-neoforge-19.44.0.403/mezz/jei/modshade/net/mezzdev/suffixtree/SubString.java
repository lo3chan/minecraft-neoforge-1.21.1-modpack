package mezz.jei.modshade.net.mezzdev.suffixtree;

public class SubString implements CharSequence {
   private String string;
   private int offset;
   private int length;

   public SubString(String string) {
      this(string, 0, string.length());
   }

   public SubString(SubString other) {
      this(other.string, other.offset, other.length);
   }

   public SubString(String string, int offset) {
      this(string, offset, string.length() - offset);
   }

   public SubString(String string, int offset, int length) {
      if (length < 0) {
         throw new IllegalArgumentException("length (" + length + ") must be greater than or equal to 0 ");
      } else if (offset < 0) {
         throw new IllegalArgumentException("offset (" + offset + ") must be greater than or equal to 0 ");
      } else if (offset + length > string.length()) {
         throw new IllegalArgumentException(
            "offset (" + offset + ") plus length (" + length + ") must be less than or equal to the string's length (" + string.length() + ")"
         );
      } else {
         this.string = string;
         this.offset = offset;
         this.length = length;
      }
   }

   public SubString subSequence(int start) {
      return this.subSequence(start, this.length);
   }

   public SubString subSequence(int start, int end) {
      if (start < 0 || start > end || end > this.length) {
         throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", length " + this.length);
      } else {
         return start == 0 && end == this.length ? this : new SubString(this.string, this.offset + start, end - start);
      }
   }

   @Override
   public boolean isEmpty() {
      return this.length == 0;
   }

   @Override
   public char charAt(int index) {
      return this.string.charAt(this.offset + index);
   }

   @Override
   public int length() {
      return this.length;
   }

   public void set(SubString other) {
      this.string = other.string;
      this.offset = other.offset;
      this.length = other.length;
   }

   public SubString shorten(int amount) {
      if (amount < 0) {
         throw new IllegalArgumentException("amount (" + amount + ") must be greater than or equal to 0 ");
      } else if (this.length != 0 && amount != 0) {
         int newLength = Math.max(this.length - amount, 0);
         return new SubString(this.string, this.offset, newLength);
      } else {
         return this;
      }
   }

   public SubString extend(char newChar) {
      if (this.offset + this.length >= this.string.length()) {
         throw new IndexOutOfBoundsException("cannot extend the string past its maximum length " + this.length);
      } else {
         char expectedChar = this.charAt(this.length);
         if (expectedChar != newChar) {
            throw new IllegalArgumentException(
               "extend must be called with the next char. expected '" + expectedChar + "' but was given '" + newChar + "' instead."
            );
         } else {
            return new SubString(this.string, this.offset, this.length + 1);
         }
      }
   }

   public boolean startsWith(SubString prefix) {
      return this.startsWith(prefix, prefix.length());
   }

   public boolean startsWith(SubString prefix, int lenToMatch) {
      if (lenToMatch > this.length) {
         return false;
      } else {
         return this.string == prefix.string && this.offset == prefix.offset
            ? true
            : this.string.regionMatches(this.offset, prefix.string, prefix.offset, lenToMatch);
      }
   }

   @Override
   public String toString() {
      return this.string.substring(this.offset, this.offset + this.length);
   }

   public String debugString() {
      return this.getClass().getSimpleName() + ": \"" + this + "\"\nBacking string: \"" + this.string + "\"";
   }
}
