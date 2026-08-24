package net.mehvahdjukaar.moonlight.api.util.math;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class CircularGridUtils {
   public static void forEachInRing(int cx, int cy, int R, int gridScale, CircularGridUtils.IntVec2iConsumer consumer) {
      if (R >= 0) {
         R = Math.floorDiv(R, gridScale);
         if (R == 0) {
            consumer.accept(cx * gridScale, cy * gridScale);
         } else {
            long Rl = R;
            long outerBound = (Rl + 1L) * (Rl + 1L) - 1L;
            long innerBound = Rl * Rl - 1L;
            int maxDy = (int)Math.floor(Math.sqrt(outerBound));
            int xOuter = R + 1;
            int xInner = R;

            for (int dy = 0; dy <= maxDy; dy++) {
               long dy2 = (long)dy * dy;

               while (xOuter >= 0 && (long)xOuter * xOuter + dy2 > outerBound) {
                  xOuter--;
               }

               if (innerBound < 0L) {
                  xInner = -1;
               } else {
                  while (xInner >= 0 && (long)xInner * xInner + dy2 > innerBound) {
                     xInner--;
                  }
               }

               int xLow = xInner + 1;
               int xHigh = xOuter;
               if (xOuter >= xLow) {
                  int yPos = (cy + dy) * gridScale;
                  int yNeg = (cy - dy) * gridScale;
                  if (dy == 0) {
                     for (int x = xLow; x <= xHigh; x++) {
                        int xp = (cx + x) * gridScale;
                        int xn = (cx - x) * gridScale;
                        consumer.accept(xp, yPos);
                        if (x != 0) {
                           consumer.accept(xn, yPos);
                        }
                     }
                  } else {
                     for (int xx = xLow; xx <= xHigh; xx++) {
                        int xp = (cx + xx) * gridScale;
                        int xn = (cx - xx) * gridScale;
                        consumer.accept(xp, yPos);
                        if (xx != 0) {
                           consumer.accept(xn, yPos);
                        }

                        consumer.accept(xp, yNeg);
                        if (xx != 0) {
                           consumer.accept(xn, yNeg);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void forEachInRing(int cx, int cy, int R, CircularGridUtils.IntVec2iConsumer consumer) {
      forEachInRing(cx, cy, R, 1, consumer);
   }

   public static Iterator<Vec2i> iterateInRing(int cx, int cy, int R) {
      return new CircularGridUtils.RingIterator(cx, cy, R, 1);
   }

   public static Iterator<Vec2i> iterateInRing(int cx, int cy, int R, int gridScale) {
      return new CircularGridUtils.RingIterator(cx, cy, R, gridScale);
   }

   public static void forEachInDisk(int centerX, int centerY, int radius, CircularGridUtils.IntVec2iConsumer consumer) {
      forEachInDisk(centerX, centerY, radius, 1, consumer);
   }

   public static void forEachInDisk(int centerX, int centerY, int radius, int gridScale, CircularGridUtils.IntVec2iConsumer consumer) {
      if (radius <= 0) {
         consumer.accept(centerX * gridScale, centerY * gridScale);
      } else {
         int rSq = radius * radius;

         for (int dy = -radius; dy <= radius; dy++) {
            int yy = dy * dy;
            int dx = (int)Math.floor(Math.sqrt(rSq - yy));
            int startX = (centerX - dx) * gridScale;
            int endX = (centerX + dx) * gridScale;
            int py = (centerY + dy) * gridScale;

            for (int x = startX; x <= endX; x += gridScale) {
               consumer.accept(x, py);
            }
         }
      }
   }

   @FunctionalInterface
   public interface IntVec2iConsumer {
      void accept(int var1, int var2);
   }

   private static class RingIterator implements Iterator<Vec2i> {
      private final int cx;
      private final int cy;
      private final int gridScale;
      private final long Rl;
      private final long outerBound;
      private final long innerBound;
      private final int maxDy;
      private int dy = 0;
      private int xOuter;
      private int xInner;
      private int currentX;
      private int currentXLow;
      private int currentXHigh;
      private int pointsGenerated = 0;
      private int pointsInCurrentRow = 0;
      private boolean initialized = false;
      private boolean hasNext = true;
      private boolean yPositive = true;
      private boolean xPositive = true;

      RingIterator(int cx, int cy, int R, int gridScale) {
         this.cx = cx;
         this.cy = cy;
         this.gridScale = gridScale;
         R = Math.floorDiv(R, gridScale);
         if (R == 0) {
            this.Rl = 0L;
            this.outerBound = 0L;
            this.innerBound = -1L;
            this.maxDy = 0;
            this.xOuter = 0;
            this.xInner = -1;
            this.currentXLow = 0;
            this.currentXHigh = 0;
            this.initialized = true;
            this.pointsInCurrentRow = 1;
         } else {
            this.Rl = R;
            this.outerBound = (this.Rl + 1L) * (this.Rl + 1L) - 1L;
            this.innerBound = this.Rl * this.Rl - 1L;
            this.maxDy = (int)Math.floor(Math.sqrt(this.outerBound));
            this.xOuter = R + 1;
            this.xInner = R;
         }
      }

      private void init() {
         if (!this.initialized) {
            this.computeXBoundsForCurrentDy();
            if (this.currentXHigh >= this.currentXLow) {
               this.currentX = this.currentXLow;
               this.pointsInCurrentRow = this.computePointsForCurrentX();
            } else {
               this.advanceDy();
            }

            this.initialized = true;
         }
      }

      private void computeXBoundsForCurrentDy() {
         long dy2 = (long)this.dy * this.dy;

         while (this.xOuter >= 0 && (long)this.xOuter * this.xOuter + dy2 > this.outerBound) {
            this.xOuter--;
         }

         if (this.innerBound < 0L) {
            this.xInner = -1;
         } else {
            while (this.xInner >= 0 && (long)this.xInner * this.xInner + dy2 > this.innerBound) {
               this.xInner--;
            }
         }

         this.currentXLow = this.xInner + 1;
         this.currentXHigh = this.xOuter;
      }

      private int computePointsForCurrentX() {
         if (this.dy == 0) {
            return this.currentX == 0 ? 1 : 2;
         } else {
            return this.currentX == 0 ? 2 : 4;
         }
      }

      private void advanceX() {
         this.currentX++;
         if (this.currentX > this.currentXHigh) {
            this.advanceDy();
         } else {
            this.pointsInCurrentRow = this.computePointsForCurrentX();
            this.pointsGenerated = 0;
            this.yPositive = true;
            this.xPositive = true;
         }
      }

      private void advanceDy() {
         this.dy++;
         if (this.dy > this.maxDy) {
            this.hasNext = false;
         } else {
            this.computeXBoundsForCurrentDy();
            if (this.currentXHigh >= this.currentXLow) {
               this.currentX = this.currentXLow;
               this.pointsInCurrentRow = this.computePointsForCurrentX();
               this.pointsGenerated = 0;
               this.yPositive = true;
               this.xPositive = true;
            } else {
               this.advanceDy();
            }
         }
      }

      @Override
      public boolean hasNext() {
         if (!this.initialized) {
            this.init();
         }

         return this.hasNext;
      }

      public Vec2i next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else if (this.Rl == 0L) {
            this.hasNext = false;
            return new Vec2i(this.cx * this.gridScale, this.cy * this.gridScale);
         } else {
            int x;
            int y;
            if (this.dy == 0) {
               if (this.currentX == 0) {
                  x = this.cx * this.gridScale;
                  y = this.cy * this.gridScale;
                  this.advanceX();
               } else if (this.xPositive) {
                  x = (this.cx + this.currentX) * this.gridScale;
                  y = this.cy * this.gridScale;
                  this.xPositive = false;
               } else {
                  x = (this.cx - this.currentX) * this.gridScale;
                  y = this.cy * this.gridScale;
                  this.advanceX();
               }
            } else if (this.currentX == 0) {
               if (this.yPositive) {
                  x = this.cx * this.gridScale;
                  y = (this.cy + this.dy) * this.gridScale;
                  this.yPositive = false;
               } else {
                  x = this.cx * this.gridScale;
                  y = (this.cy - this.dy) * this.gridScale;
                  this.advanceX();
               }
            } else if (this.yPositive) {
               if (this.xPositive) {
                  x = (this.cx + this.currentX) * this.gridScale;
                  y = (this.cy + this.dy) * this.gridScale;
                  this.xPositive = false;
               } else {
                  x = (this.cx - this.currentX) * this.gridScale;
                  y = (this.cy + this.dy) * this.gridScale;
                  this.yPositive = false;
                  this.xPositive = true;
               }
            } else if (this.xPositive) {
               x = (this.cx + this.currentX) * this.gridScale;
               y = (this.cy - this.dy) * this.gridScale;
               this.xPositive = false;
            } else {
               x = (this.cx - this.currentX) * this.gridScale;
               y = (this.cy - this.dy) * this.gridScale;
               this.advanceX();
            }

            return new Vec2i(x, y);
         }
      }
   }
}
