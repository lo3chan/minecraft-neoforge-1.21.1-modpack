package org.tukaani.xz;

import java.io.InputStream;

public class DeltaOptions extends FilterOptions {
   public static final int DISTANCE_MIN = 1;
   public static final int DISTANCE_MAX = 256;
   private int distance = 1;

   public DeltaOptions() {
   }

   public DeltaOptions(int i) throws UnsupportedOptionsException {
      this.setDistance(i);
   }

   public void setDistance(int i) throws UnsupportedOptionsException {
      if (i >= 1 && i <= 256) {
         this.distance = i;
      } else {
         throw new UnsupportedOptionsException("Delta distance must be in the range [1, 256]: " + i);
      }
   }

   public int getDistance() {
      return this.distance;
   }

   @Override
   public int getEncoderMemoryUsage() {
      return DeltaOutputStream.getMemoryUsage();
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return new DeltaOutputStream(finishableOutputStream, this);
   }

   @Override
   public int getDecoderMemoryUsage() {
      return 1;
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      return new DeltaInputStream(inputStream, this.distance);
   }

   @Override
   FilterEncoder getFilterEncoder() {
      return new DeltaEncoder(this);
   }

   @Override
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         assert false;

         throw new RuntimeException();
      }
   }
}
