package com.seibel.distanthorizons.core.util.gridList;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PosArrayGridList<T> extends ArrayGridList<T> {
   private int offsetX;
   private int offsetY;

   public PosArrayGridList(int gridSize, int offsetX, int offsetY, BiFunction<Integer, Integer, T> filler) {
      super(gridSize, filler);
      this.offsetX = offsetX;
      this.offsetY = offsetY;
   }

   public PosArrayGridList(int gridSize, int offsetX, int offsetY) {
      this(gridSize, offsetX, offsetY, (x, y) -> null);
   }

   public PosArrayGridList(PosArrayGridList<T> copy) {
      super(copy);
      this.offsetX = copy.offsetX;
      this.offsetY = copy.offsetY;
   }

   public PosArrayGridList(PosArrayGridList<T> source, int minR, int maxR) {
      super(source, minR, maxR);
   }

   @Override
   protected int getIndexOf(int x, int y) {
      return x - this.offsetX + (y - this.offsetY) * this.gridSize;
   }

   @Override
   public void forEachPos(BiConsumer<Integer, Integer> consumer) {
      for (int y = this.offsetY; y < this.offsetY + this.gridSize; y++) {
         for (int x = this.offsetX; x < this.offsetX + this.gridSize; x++) {
            consumer.accept(x, y);
         }
      }
   }

   public int getOffsetX() {
      return this.offsetX;
   }

   public int getOffsetY() {
      return this.offsetY;
   }

   @Override
   public boolean inRange(int x, int y) {
      return x >= this.offsetX && x < this.offsetX + this.gridSize && y >= this.offsetY && y < this.offsetY + this.gridSize;
   }

   private T _directGet(int x, int y) {
      return !this.inRange(x, y) ? null : this.get(x, y);
   }

   public boolean move(int deltaX, int deltaY, Consumer<? super T> dealloc) {
      if (deltaX == 0 && deltaY == 0) {
         return false;
      } else if (Math.abs(deltaX) < this.gridSize && Math.abs(deltaY) < this.gridSize) {
         int newMinX = this.offsetX + deltaX;
         int newMinY = this.offsetY + deltaY;
         int newMaxX = newMinX + this.gridSize;
         int newMaxY = newMinY + this.gridSize;
         if (dealloc != null) {
            this.forEachPos((xx, yx) -> {
               if (xx < newMinX || yx < newMinY || xx >= newMaxX || yx >= newMaxY) {
                  T t = this.get(xx, yx);
                  if (t != null) {
                     dealloc.accept(t);
                  }
               }
            });
         }

         this.offsetX = newMinX;
         this.offsetY = newMinY;
         if (deltaX >= 0 && deltaY >= 0) {
            for (int x = newMinX; x < newMaxX; x++) {
               for (int y = newMinY; y < newMaxY; y++) {
                  this.set(x, y, this._directGet(x + deltaX, y + deltaY));
               }
            }
         } else if (deltaX < 0 && deltaY >= 0) {
            for (int x = newMaxX - 1; x >= newMinX; x--) {
               for (int y = newMinY; y < newMaxY; y++) {
                  this.set(x, y, this._directGet(x + deltaX, y + deltaY));
               }
            }
         } else if (deltaX >= 0) {
            for (int x = newMinX; x < newMaxX; x++) {
               for (int y = newMaxY - 1; y >= newMinY; y--) {
                  this.set(x, y, this._directGet(x + deltaX, y + deltaY));
               }
            }
         } else {
            for (int x = newMaxX - 1; x >= newMinX; x--) {
               for (int y = newMaxY - 1; y >= newMinY; y--) {
                  this.set(x, y, this._directGet(x + deltaX, y + deltaY));
               }
            }
         }

         return true;
      } else {
         this.clear(dealloc);
         this.offsetX += deltaX;
         this.offsetY += deltaY;
         return true;
      }
   }

   @Override
   public String toString() {
      return this.getClass().toString() + "[" + this.offsetX + "," + this.offsetY + "] " + this.gridSize + "*" + this.gridSize + "[" + this.size() + "]";
   }
}
