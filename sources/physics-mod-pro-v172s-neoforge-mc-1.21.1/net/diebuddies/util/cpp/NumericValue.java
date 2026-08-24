package net.diebuddies.util.cpp;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.CheckForNull;
import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class NumericValue extends Number {
   public static final int F_UNSIGNED = 1;
   public static final int F_INT = 2;
   public static final int F_LONG = 4;
   public static final int F_LONGLONG = 8;
   public static final int F_FLOAT = 16;
   public static final int F_DOUBLE = 32;
   public static final int FF_SIZE = 62;
   private final int base;
   private final String integer;
   private String fraction;
   private int expbase = 0;
   private String exponent;
   private int flags;

   public NumericValue(@Nonnegative int base, @Nonnull String integer) {
      this.base = base;
      this.integer = integer;
   }

   @Nonnegative
   public int getBase() {
      return this.base;
   }

   @Nonnull
   public String getIntegerPart() {
      return this.integer;
   }

   @CheckForNull
   public String getFractionalPart() {
      return this.fraction;
   }

   void setFractionalPart(@Nonnull String fraction) {
      this.fraction = fraction;
   }

   @CheckForSigned
   public int getExponentBase() {
      return this.expbase;
   }

   @CheckForNull
   public String getExponent() {
      return this.exponent;
   }

   void setExponent(@Nonnegative int expbase, @Nonnull String exponent) {
      this.expbase = expbase;
      this.exponent = exponent;
   }

   public int getFlags() {
      return this.flags;
   }

   void setFlags(int flags) {
      this.flags = flags;
   }

   @Nonnull
   public BigDecimal toBigDecimal() {
      int scale = 0;
      String text = this.getIntegerPart();
      String t_fraction = this.getFractionalPart();
      if (t_fraction != null) {
         text = text + this.getFractionalPart();
         scale += t_fraction.length();
      }

      String t_exponent = this.getExponent();
      if (t_exponent != null) {
         scale -= Integer.parseInt(t_exponent);
      }

      BigInteger unscaled = new BigInteger(text, this.getBase());
      return new BigDecimal(unscaled, scale);
   }

   @Nonnull
   public Number toJavaLangNumber() {
      int flags = this.getFlags();
      if ((flags & 32) != 0) {
         return this.doubleValue();
      } else if ((flags & 16) != 0) {
         return this.floatValue();
      } else if ((flags & 12) != 0) {
         return this.longValue();
      } else if ((flags & 2) != 0) {
         return this.intValue();
      } else if (this.getFractionalPart() != null) {
         return this.doubleValue();
      } else if (this.getExponent() != null) {
         return this.doubleValue();
      } else {
         long value = this.longValue();
         return (Number)(value <= 2147483647L && value >= -2147483648L ? (int)value : value);
      }
   }

   private int exponentValue() {
      return Integer.parseInt(this.exponent, 10);
   }

   @Override
   public int intValue() {
      int v = this.integer.length() == 0 ? 0 : Integer.parseInt(this.integer, this.base);
      if (this.expbase == 2) {
         v <<= this.exponentValue();
      } else if (this.expbase != 0) {
         v = (int)(v * Math.pow(this.expbase, this.exponentValue()));
      }

      return v;
   }

   @Override
   public long longValue() {
      long v = this.integer.length() == 0 ? 0L : Long.parseLong(this.integer, this.base);
      if (this.expbase == 2) {
         v <<= this.exponentValue();
      } else if (this.expbase != 0) {
         v = (long)(v * Math.pow(this.expbase, this.exponentValue()));
      }

      return v;
   }

   @Override
   public float floatValue() {
      return this.getBase() != 10 ? (float)this.longValue() : Float.parseFloat(this.toString());
   }

   @Override
   public double doubleValue() {
      return this.getBase() != 10 ? this.longValue() : Double.parseDouble(this.toString());
   }

   private boolean appendFlags(StringBuilder buf, String suffix, int flag) {
      if ((this.getFlags() & flag) != flag) {
         return false;
      } else {
         buf.append(suffix);
         return true;
      }
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      switch (this.base) {
         case 2:
            buf.append('b');
            break;
         case 8:
            buf.append('0');
         case 10:
            break;
         case 16:
            buf.append("0x");
            break;
         default:
            buf.append("[base-").append(this.base).append("]");
      }

      buf.append(this.getIntegerPart());
      if (this.getFractionalPart() != null) {
         buf.append('.').append(this.getFractionalPart());
      }

      if (this.getExponent() != null) {
         buf.append((char)(this.base > 10 ? 'p' : 'e'));
         buf.append(this.getExponent());
      }

      return buf.toString();
   }
}
