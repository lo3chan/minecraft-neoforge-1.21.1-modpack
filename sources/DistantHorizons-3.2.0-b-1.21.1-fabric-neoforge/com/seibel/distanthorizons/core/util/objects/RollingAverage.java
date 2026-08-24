package com.seibel.distanthorizons.core.util.objects;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RollingAverage {
   private final double[] values;
   private final int maxSize;
   private int currentSize = 0;
   private int index = 0;
   private double sum = 0.0;
   private final Lock arrayLock = new ReentrantLock();
   private long lifetimeCount = 0L;

   public RollingAverage(int size) {
      if (size <= 0) {
         throw new IllegalArgumentException("Size must be greater than 0");
      } else {
         this.maxSize = size;
         this.values = new double[size];
      }
   }

   public void add(double value) {
      this.arrayLock.lock();

      try {
         this.sum = this.sum - this.values[this.index];
         this.values[this.index] = value;
         this.sum += value;
         this.index = (this.index + 1) % this.maxSize;
         this.currentSize = Math.max(this.index + 1, this.currentSize);
         this.lifetimeCount++;
      } finally {
         this.arrayLock.unlock();
      }
   }

   public void clear() {
      this.arrayLock.lock();

      try {
         this.sum = 0.0;
         this.index = 0;
         this.currentSize = 0;
         this.lifetimeCount = 0L;
         Arrays.fill(this.values, 0.0);
      } finally {
         this.arrayLock.unlock();
      }
   }

   public double getAverage() {
      this.arrayLock.lock();

      double var1;
      try {
         var1 = this.sum / this.currentSize;
      } finally {
         this.arrayLock.unlock();
      }

      return var1;
   }

   public String getAverageRoundedString() {
      return String.format("%.2f", this.getAverage());
   }

   public long getLifetimeCount() {
      return this.lifetimeCount;
   }

   @Override
   public String toString() {
      return "avg: [" + this.getAverageRoundedString() + "], count: [" + this.currentSize + "], max count: [" + this.maxSize + "].";
   }
}
