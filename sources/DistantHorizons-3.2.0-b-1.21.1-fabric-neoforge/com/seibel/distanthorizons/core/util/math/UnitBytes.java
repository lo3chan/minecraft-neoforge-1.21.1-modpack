package com.seibel.distanthorizons.core.util.math;

import java.util.Objects;

public class UnitBytes {
   public final long value;

   public UnitBytes(long value) {
      this.value = value;
   }

   public long value() {
      return this.value;
   }

   public static long byteToGB(long v) {
      return v / 1073741824L;
   }

   public static long byteToMB(long v) {
      return v / 1048576L;
   }

   public static long byteToKB(long v) {
      return v / 1024L;
   }

   public static long GBToByte(long v) {
      return v * 1073741824L;
   }

   public static long MBToByte(long v) {
      return v * 1048576L;
   }

   public static long KBToByte(long v) {
      return v * 1024L;
   }

   @Override
   public String toString() {
      long v = this.value;
      StringBuilder str = new StringBuilder();
      long GB = byteToGB(v);
      if (GB != 0L) {
         str.append(GB).append("GB ");
      }

      v -= GBToByte(GB);
      long MB = byteToMB(v);
      if (MB != 0L) {
         str.append(MB).append("MB ");
      }

      v -= MBToByte(MB);
      long KB = byteToKB(v);
      if (KB != 0L) {
         str.append(KB).append("KB ");
      }

      v -= KBToByte(KB);
      str.append(v).append("B");
      return str.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UnitBytes unitBytes = (UnitBytes)o;
         return this.value == unitBytes.value;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value);
   }
}
