package com.seibel.distanthorizons.core.util.gridList;

import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.coreapi.ModInfo;
import com.seibel.distanthorizons.coreapi.util.MathUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MovableGridRingList<T> extends ArrayList<T> implements List<T> {
   private final AtomicReference<MovableGridRingList.Pos2D> minPosRef = new AtomicReference<>();
   private final int width;
   private final int halfWidth;
   private final ReentrantReadWriteLock moveLock = new ReentrantReadWriteLock();
   private final MovableGridRingList.Pos2D[] ringPositionIteratorArray;

   public MovableGridRingList(int halfWidth, int centerX, int centerY) {
      super((halfWidth * 2 + 1) * (halfWidth * 2 + 1));
      this.width = halfWidth * 2 + 1;
      this.halfWidth = halfWidth;
      this.minPosRef.set(new MovableGridRingList.Pos2D(centerX - halfWidth, centerY - halfWidth));
      this.ringPositionIteratorArray = this.createRingIteratorList();
      this.clear();
   }

   private MovableGridRingList.Pos2D[] createRingIteratorList() {
      MovableGridRingList.Pos2D[] posArray = new MovableGridRingList.Pos2D[this.width * this.width];
      int i = 0;

      for (int xPos = -this.halfWidth; xPos <= this.halfWidth; xPos++) {
         for (int zPos = -this.halfWidth; zPos <= this.halfWidth; zPos++) {
            posArray[i] = new MovableGridRingList.Pos2D(xPos, zPos);
            i++;
         }
      }

      Arrays.sort(posArray, (a, b) -> {
         long disSqrA = (long)a.getX() * a.getX() + (long)a.getY() * a.getY();
         long disSqrB = (long)b.getX() * b.getX() + (long)b.getY() * b.getY();
         return Double.compare(disSqrA, disSqrB);
      });
      MovableGridRingList.Pos2D halfPos = new MovableGridRingList.Pos2D(this.halfWidth, this.halfWidth);

      for (int j = 0; j < posArray.length; j++) {
         posArray[j] = posArray[j].add(halfPos);
      }

      if (ModInfo.IS_DEV_BUILD) {
         for (MovableGridRingList.Pos2D pos2D : posArray) {
            LodUtil.assertTrue(pos2D.getX() >= 0 && pos2D.getX() < this.width);
            LodUtil.assertTrue(pos2D.getY() >= 0 && pos2D.getY() < this.width);
         }
      }

      return posArray;
   }

   public T get(MovableGridRingList.Pos2D pos) {
      return this.get(pos.getX(), pos.getY());
   }

   public T get(int x, int y) {
      MovableGridRingList.Pos2D min = this.minPosRef.get();
      if (!this.inRangeAcquired(x, y, min)) {
         return null;
      } else {
         this.moveLock.readLock().lock();

         Object var5;
         try {
            MovableGridRingList.Pos2D newMin = this.minPosRef.get();
            if (min == newMin || this.inRangeAcquired(x, y, newMin)) {
               return this.getUnsafe(x, y);
            }

            var5 = null;
         } finally {
            this.moveLock.readLock().unlock();
         }

         return (T)var5;
      }
   }

   public boolean set(MovableGridRingList.Pos2D pos, T item) {
      return this.set(pos.getX(), pos.getY(), item);
   }

   public boolean set(int x, int y, T item) {
      MovableGridRingList.Pos2D min = this.minPosRef.get();
      if (!this.inRangeAcquired(x, y, min)) {
         return false;
      } else {
         this.moveLock.readLock().lock();

         boolean var6;
         try {
            MovableGridRingList.Pos2D newMin = this.minPosRef.get();
            if (min == newMin || this.inRangeAcquired(x, y, newMin)) {
               this.setUnsafe(x, y, item);
               return true;
            }

            var6 = false;
         } finally {
            this.moveLock.readLock().unlock();
         }

         return var6;
      }
   }

   public T swap(MovableGridRingList.Pos2D pos, T item) {
      return this.swap(pos.getX(), pos.getY(), item);
   }

   public T swap(int x, int y, T item) {
      MovableGridRingList.Pos2D min = this.minPosRef.get();
      if (!this.inRangeAcquired(x, y, min)) {
         return item;
      } else {
         this.moveLock.readLock().lock();

         Object var6;
         try {
            MovableGridRingList.Pos2D newMin = this.minPosRef.get();
            if (min == newMin || this.inRangeAcquired(x, y, newMin)) {
               return this.swapUnsafe(x, y, item);
            }

            var6 = item;
         } finally {
            this.moveLock.readLock().unlock();
         }

         return (T)var6;
      }
   }

   public T remove(MovableGridRingList.Pos2D pos) {
      return this.remove(pos.getX(), pos.getY());
   }

   public T remove(int x, int y) {
      return this.swap(x, y, null);
   }

   @Override
   public void clear() {
      this.clear(null);
   }

   public void clear(Consumer<? super T> removedItemConsumer) {
      this.moveLock.writeLock().lock();

      try {
         if (removedItemConsumer != null) {
            super.forEach(item -> {
               if (item != null) {
                  removedItemConsumer.accept((T)item);
               }
            });
         }

         super.clear();
         super.ensureCapacity(this.width * this.width);

         for (int i = 0; i < this.width * this.width; i++) {
            super.add(null);
         }
      } finally {
         this.moveLock.writeLock().unlock();
      }
   }

   public boolean moveTo(int newCenterX, int newCenterY) {
      return this.moveTo(newCenterX, newCenterY, null);
   }

   public boolean moveTo(int newCenterX, int newCenterY, Consumer<? super T> removedItemConsumer) {
      return this.moveTo(newCenterX, newCenterY, removedItemConsumer, null);
   }

   public boolean moveTo(
      int newCenterX, int newCenterY, Consumer<? super T> removedItemConsumer, BiConsumer<MovableGridRingList.Pos2D, ? super T> nullableRemovedItemConsumer
   ) {
      MovableGridRingList.Pos2D cPos = this.minPosRef.get();
      int newMinX = newCenterX - this.halfWidth;
      int newMinY = newCenterY - this.halfWidth;
      if (cPos.getX() == newMinX && cPos.getY() == newMinY) {
         return false;
      } else {
         this.moveLock.writeLock().lock();

         boolean x;
         try {
            cPos = this.minPosRef.get();
            int deltaX = newMinX - cPos.getX();
            int deltaY = newMinY - cPos.getY();
            if (deltaX != 0 || deltaY != 0) {
               if (Math.abs(deltaX) < this.width && Math.abs(deltaY) < this.width) {
                  for (int xx = 0; xx < this.width; xx++) {
                     for (int y = 0; y < this.width; y++) {
                        MovableGridRingList.Pos2D itemPos = new MovableGridRingList.Pos2D(xx + cPos.getX(), y + cPos.getY());
                        if (xx - deltaX < 0 || y - deltaY < 0 || xx - deltaX >= this.width || y - deltaY >= this.width) {
                           T item = this.swapUnsafe(itemPos.getX(), itemPos.getY(), null);
                           if (item != null && removedItemConsumer != null) {
                              removedItemConsumer.accept(item);
                           }

                           if (nullableRemovedItemConsumer != null) {
                              nullableRemovedItemConsumer.accept(itemPos, item);
                           }
                        } else if (nullableRemovedItemConsumer != null) {
                           nullableRemovedItemConsumer.accept(itemPos, null);
                        }
                     }
                  }
               } else {
                  this.clear(removedItemConsumer);
               }

               this.minPosRef.set(new MovableGridRingList.Pos2D(newMinX, newMinY));
               return true;
            }

            x = false;
         } finally {
            this.moveLock.writeLock().unlock();
         }

         return x;
      }
   }

   public MovableGridRingList.Pos2D getCenter() {
      return new MovableGridRingList.Pos2D(this.minPosRef.get().getX() + this.halfWidth, this.minPosRef.get().getY() + this.halfWidth);
   }

   public MovableGridRingList.Pos2D getMinPosInRange() {
      return this.minPosRef.get();
   }

   public MovableGridRingList.Pos2D getMaxPosInRange() {
      return new MovableGridRingList.Pos2D(this.minPosRef.get().getX() + this.width - 1, this.minPosRef.get().getY() + this.width - 1);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHalfWidth() {
      return this.halfWidth;
   }

   public boolean inRange(int x, int y) {
      MovableGridRingList.Pos2D minPos = this.minPosRef.get();
      return x >= minPos.getX() && x < minPos.getX() + this.width && y >= minPos.getY() && y < minPos.getY() + this.width;
   }

   private boolean inRangeAcquired(int x, int y, MovableGridRingList.Pos2D min) {
      return x >= min.getX() && x < min.getX() + this.width && y >= min.getY() && y < min.getY() + this.width;
   }

   private T getUnsafe(int x, int y) {
      return (T)super.get(Math.floorMod(x, this.width) + Math.floorMod(y, this.width) * this.width);
   }

   private void setUnsafe(int x, int y, T item) {
      super.set(Math.floorMod(x, this.width) + Math.floorMod(y, this.width) * this.width, item);
   }

   private T swapUnsafe(int x, int y, T item) {
      return super.set(Math.floorMod(x, this.width) + Math.floorMod(y, this.width) * this.width, item);
   }

   public void forEachPos(BiConsumer<? super T, MovableGridRingList.Pos2D> consumer) {
      this.moveLock.readLock().lock();

      try {
         MovableGridRingList.Pos2D min = this.minPosRef.get();

         for (int x = min.getX(); x < min.getX() + this.width; x++) {
            for (int y = min.getY(); y < min.getY() + this.width; y++) {
               T t = this.getUnsafe(x, y);
               consumer.accept(t, new MovableGridRingList.Pos2D(x, y));
            }
         }
      } finally {
         this.moveLock.readLock().unlock();
      }
   }

   public void forEachOrdered(Consumer<? super T> consumer) {
      this.moveLock.readLock().lock();

      try {
         MovableGridRingList.Pos2D min = this.minPosRef.get();

         for (MovableGridRingList.Pos2D offset : this.ringPositionIteratorArray) {
            T item = this.getUnsafe(min.getX() + offset.getX(), min.getY() + offset.getY());
            if (item != null) {
               consumer.accept(item);
            }
         }
      } finally {
         this.moveLock.readLock().unlock();
      }
   }

   public void forEachPosOrdered(BiConsumer<? super T, MovableGridRingList.Pos2D> consumer) {
      this.moveLock.readLock().lock();

      try {
         MovableGridRingList.Pos2D min = this.minPosRef.get();

         for (MovableGridRingList.Pos2D offset : this.ringPositionIteratorArray) {
            LodUtil.assertTrue(this.inRangeAcquired(min.getX() + offset.getX(), min.getY() + offset.getY(), min));
            T item = this.getUnsafe(min.getX() + offset.getX(), min.getY() + offset.getY());
            consumer.accept(item, new MovableGridRingList.Pos2D(min.getX() + offset.getX(), min.getY() + offset.getY()));
         }
      } finally {
         this.moveLock.readLock().unlock();
      }
   }

   @Override
   public String toString() {
      MovableGridRingList.Pos2D p = this.minPosRef.get();
      return this.getClass().getSimpleName()
         + "["
         + (p.getX() + this.halfWidth)
         + ","
         + (p.getY() + this.halfWidth)
         + "] "
         + this.width
         + "*"
         + this.width
         + "["
         + this.size()
         + "]";
   }

   public String toDetailString() {
      StringBuilder str = new StringBuilder("\n");
      int i = 0;
      str.append(this);
      str.append("\n");

      for (T t : this) {
         str.append(t != null ? t.toString() : "NULL");
         str.append(", ");
         if (++i % this.width == 0) {
            str.append("\n");
         }
      }

      return str.toString();
   }

   public static class Pos2D {
      public static final MovableGridRingList.Pos2D ZERO = new MovableGridRingList.Pos2D(0, 0);
      private final int x;
      private final int y;

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public Pos2D(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public MovableGridRingList.Pos2D add(MovableGridRingList.Pos2D other) {
         return new MovableGridRingList.Pos2D(this.x + other.x, this.y + other.y);
      }

      public MovableGridRingList.Pos2D subtract(MovableGridRingList.Pos2D other) {
         return new MovableGridRingList.Pos2D(this.x - other.x, this.y - other.y);
      }

      public MovableGridRingList.Pos2D subtract(int value) {
         return new MovableGridRingList.Pos2D(this.x - value, this.y - value);
      }

      public double dist(MovableGridRingList.Pos2D other) {
         return Math.sqrt(Math.pow(this.x - other.x, 2.0) + Math.pow(this.y - other.y, 2.0));
      }

      public long distSquared(MovableGridRingList.Pos2D other) {
         return MathUtil.pow2((long)this.x - other.x) + MathUtil.pow2((long)this.y - other.y);
      }

      public int chebyshevDist(MovableGridRingList.Pos2D other) {
         return Math.max(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
      }

      public int manhattanDist(MovableGridRingList.Pos2D other) {
         return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.x, this.y);
      }

      @Override
      public String toString() {
         return "[" + this.x + ", " + this.y + "]";
      }

      @Override
      public boolean equals(Object otherObj) {
         if (otherObj == this) {
            return true;
         } else if (!(otherObj instanceof MovableGridRingList.Pos2D)) {
            return false;
         } else {
            MovableGridRingList.Pos2D otherPos = (MovableGridRingList.Pos2D)otherObj;
            return this.x == otherPos.x && this.y == otherPos.y;
         }
      }
   }
}
