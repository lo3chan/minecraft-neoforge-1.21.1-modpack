/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.CheckForSigned
 *  javax.annotation.Nonnegative
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.CheckForNull;
import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class NumericValue
extends Number {
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
        String t_exponent;
        int scale = 0;
        Object text = this.getIntegerPart();
        String t_fraction = this.getFractionalPart();
        if (t_fraction != null) {
            text = (String)text + this.getFractionalPart();
            scale += t_fraction.length();
        }
        if ((t_exponent = this.getExponent()) != null) {
            scale -= Integer.parseInt(t_exponent);
        }
        BigInteger unscaled = new BigInteger((String)text, this.getBase());
        return new BigDecimal(unscaled, scale);
    }

    @Nonnull
    public Number toJavaLangNumber() {
        int flags = this.getFlags();
        if ((flags & 0x20) != 0) {
            return this.doubleValue();
        }
        if ((flags & 0x10) != 0) {
            return Float.valueOf(this.floatValue());
        }
        if ((flags & 0xC) != 0) {
            return this.longValue();
        }
        if ((flags & 2) != 0) {
            return this.intValue();
        }
        if (this.getFractionalPart() != null) {
            return this.doubleValue();
        }
        if (this.getExponent() != null) {
            return this.doubleValue();
        }
        long value = this.longValue();
        if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
            return (int)value;
        }
        return value;
    }

    private int exponentValue() {
        return Integer.parseInt(this.exponent, 10);
    }

    @Override
    public int intValue() {
        int v;
        int n = v = this.integer.length() == 0 ? 0 : Integer.parseInt(this.integer, this.base);
        if (this.expbase == 2) {
            v <<= this.exponentValue();
        } else if (this.expbase != 0) {
            v = (int)((double)v * Math.pow(this.expbase, this.exponentValue()));
        }
        return v;
    }

    @Override
    public long longValue() {
        long v;
        long l = v = this.integer.length() == 0 ? 0L : Long.parseLong(this.integer, this.base);
        if (this.expbase == 2) {
            v <<= this.exponentValue();
        } else if (this.expbase != 0) {
            v = (long)((double)v * Math.pow(this.expbase, this.exponentValue()));
        }
        return v;
    }

    @Override
    public float floatValue() {
        if (this.getBase() != 10) {
            return this.longValue();
        }
        return Float.parseFloat(this.toString());
    }

    @Override
    public double doubleValue() {
        if (this.getBase() != 10) {
            return this.longValue();
        }
        return Double.parseDouble(this.toString());
    }

    private boolean appendFlags(StringBuilder buf, String suffix, int flag) {
        if ((this.getFlags() & flag) != flag) {
            return false;
        }
        buf.append(suffix);
        return true;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (this.base) {
            case 8: {
                buf.append('0');
                break;
            }
            case 10: {
                break;
            }
            case 16: {
                buf.append("0x");
                break;
            }
            case 2: {
                buf.append('b');
                break;
            }
            default: {
                buf.append("[base-").append(this.base).append("]");
            }
        }
        buf.append(this.getIntegerPart());
        if (this.getFractionalPart() != null) {
            buf.append('.').append(this.getFractionalPart());
        }
        if (this.getExponent() != null) {
            buf.append(this.base > 10 ? (char)'p' : 'e');
            buf.append(this.getExponent());
        }
        return buf.toString();
    }
}

