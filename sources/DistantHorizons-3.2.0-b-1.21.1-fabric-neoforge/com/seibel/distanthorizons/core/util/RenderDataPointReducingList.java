package com.seibel.distanthorizons.core.util;

import com.google.common.annotations.VisibleForTesting;
import com.seibel.distanthorizons.core.dataObjects.render.columnViews.ColumnRenderView;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.AbstractPhantomArrayList;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import java.util.Arrays;

public class RenderDataPointReducingList extends AbstractPhantomArrayList {
   private static final boolean ASSERTS = false;
   private static final int SPECIAL_CASES = 2;
   public static final int LOWER_SHIFT = 0;
   public static final int HIGHER_SHIFT = 16;
   public static final int SMALLER_SHIFT = 32;
   public static final int BIGGER_SHIFT = 48;
   public static final int LINK_MASK = 65535;
   public static final int NULL = 65535;
   public static final long DEFAUlT_DATA = 0L;
   public static final long DEFAULT_LINKS = -1L;
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("Render Reducer");
   private short lowest;
   private short highest;
   private short smallest;
   private short biggest;
   private short sizeWithAir;
   private short sizeWithoutAir;
   private LongArrayList links;
   private LongArrayList data;
   private ShortArrayList sortingArray;

   public RenderDataPointReducingList() {
      super(ARRAY_LIST_POOL, 0, 1, 2, 0, 0);
   }

   public void populate(ColumnRenderView view) {
      int size = view.size;
      if (size == 0) {
         this.setLowest(65535);
         this.setHighest(65535);
         this.setSmallest(65535);
         this.setBiggest(65535);
         this.links = this.pooledArraysCheckout.getLongArray(0, 0);
         this.data = this.pooledArraysCheckout.getLongArray(1, 0);
         this.sortingArray = this.pooledArraysCheckout.getShortArray(0, 0);
      } else {
         int arrayCapacity = (size << 1) - 1;
         this.sortingArray = this.pooledArraysCheckout.getShortArray(0, arrayCapacity);
         this.links = this.pooledArraysCheckout.getLongArray(0, arrayCapacity);
         Arrays.fill(this.links.elements(), -1L);
         this.data = this.pooledArraysCheckout.getLongArray(1, arrayCapacity);
         int sizeWithoutAir = 0;

         for (int index = 0; index < size; index++) {
            long packedData = view.get(index);
            if (isDataVisible(packedData) && RenderDataPointUtil.getYMin(packedData) < RenderDataPointUtil.getYMax(packedData)) {
               this.setData(sizeWithoutAir, packedData);
               this.setSortingIndex(sizeWithoutAir, sizeWithoutAir);
               sizeWithoutAir++;
            }
         }

         if (sizeWithoutAir == 0) {
            this.setLowest(65535);
            this.setHighest(65535);
            this.setSmallest(65535);
            this.setBiggest(65535);
         } else {
            this.sortByPosition(sizeWithoutAir);
            int sizeWithAir = sizeWithoutAir;

            for (int sortingIndex = 1; sortingIndex < sizeWithoutAir; sortingIndex++) {
               int lowerIndex = this.getSortingIndex(sortingIndex - 1);
               int higherIndex = this.getSortingIndex(sortingIndex);
               long lowerData = this.getData(lowerIndex);
               long higherData = this.getData(higherIndex);
               int lowerMaxY = RenderDataPointUtil.getYMax(lowerData);
               int higherMinY = RenderDataPointUtil.getYMin(higherData);
               if (lowerMaxY == higherMinY) {
                  this.setHigher(lowerIndex, higherIndex);
                  this.setLower(higherIndex, lowerIndex);
               } else {
                  if (lowerMaxY >= higherMinY) {
                     throw new IllegalArgumentException(RenderDataPointUtil.toString(lowerData) + " overlaps with " + RenderDataPointUtil.toString(higherData));
                  }

                  this.setData(
                     sizeWithAir,
                     RenderDataPointUtil.createDataPoint(
                        0,
                        0,
                        0,
                        0,
                        higherMinY,
                        lowerMaxY,
                        RenderDataPointUtil.getLightSky(higherData),
                        RenderDataPointUtil.getLightBlock(higherData),
                        RenderDataPointUtil.getBlockMaterialId(higherData)
                     )
                  );
                  this.setSortingIndex(sizeWithAir, sizeWithAir);
                  this.setLower(higherIndex, sizeWithAir);
                  this.setHigher(lowerIndex, sizeWithAir);
                  this.setLower(sizeWithAir, lowerIndex);
                  this.setHigher(sizeWithAir, higherIndex);
                  sizeWithAir++;
               }
            }

            this.lowest = this.sortingArray.getShort(0);
            this.highest = this.sortingArray.getShort(sizeWithoutAir - 1);
            this.sortBySize(sizeWithAir);

            for (int sortingIndexx = 1; sortingIndexx < sizeWithAir; sortingIndexx++) {
               int smallerIndex = this.getSortingIndex(sortingIndexx - 1);
               int biggerIndex = this.getSortingIndex(sortingIndexx);
               this.setBigger(smallerIndex, biggerIndex);
               this.setSmaller(biggerIndex, smallerIndex);
            }

            this.smallest = this.sortingArray.getShort(0);
            this.biggest = this.sortingArray.getShort(sizeWithAir - 1);
            this.setSizeWithAir(sizeWithAir);
            this.setSizeWithoutAir(sizeWithoutAir);
         }
      }
   }

   public void reduce(int target) {
      if (!this.mergeVerySmallConnectedSegments(target)) {
         if (!this.mergeConnectedSegments(target)) {
            if (!this.removeLeastImportantSegments(target)) {
               this.forceBottomToMerge(target);
            }
         }
      }
   }

   @VisibleForTesting
   public void checkLinks() {
      LodUtil.assertTrue(this.getSizeWithAir() >= 0, "size with air < 0");
      LodUtil.assertTrue(this.getSizeWithoutAir() >= 0, "size without air < 0");
      LodUtil.assertTrue(this.getSizeWithoutAir() <= this.getSizeWithAir(), "more segments without air than with air");
      if (this.getSizeWithAir() == 0) {
         LodUtil.assertTrue(this.getSmallest() == 65535, "size is 0, but we have a smallest node");
         LodUtil.assertTrue(this.getBiggest() == 65535, "size is 0, but we have a biggest node");
         LodUtil.assertTrue(this.getLowest() == 65535, "size is 0, but we have a lowest node");
         LodUtil.assertTrue(this.getHighest() == 65535, "size is 0, but we have a highest node");
      } else {
         int sizeWithAir = 0;
         int sizeWithoutAir = 0;

         for (int index = this.getSmallest(); index != 65535; index = this.getBigger(index)) {
            int smaller = this.getSmaller(index);
            int bigger = this.getBigger(index);
            LodUtil.assertTrue((smaller != 65535 ? this.getBigger(smaller) : this.getSmallest()) == index, "one-way link");
            LodUtil.assertTrue((bigger != 65535 ? this.getSmaller(bigger) : this.getBiggest()) == index, "one-way link");
            LodUtil.assertTrue(smaller == 65535 || this.getSize(index) >= this.getSize(smaller), "node is not sorted by size");
            sizeWithAir++;
            if (this.isIndexVisible(index)) {
               sizeWithoutAir++;
            }
         }

         LodUtil.assertTrue(sizeWithAir == this.getSizeWithAir() && sizeWithoutAir == this.getSizeWithoutAir(), "node count does not match size");
         sizeWithoutAir = 0;
         sizeWithAir = 0;

         for (int indexx = this.getLowest(); indexx != 65535; indexx = this.getHigher(indexx)) {
            int lower = this.getLower(indexx);
            int higher = this.getHigher(indexx);
            LodUtil.assertTrue((lower != 65535 ? this.getHigher(lower) : this.getLowest()) == indexx, "one-way link");
            LodUtil.assertTrue((higher != 65535 ? this.getLower(higher) : this.getHighest()) == indexx, "one-way link");
            LodUtil.assertTrue(this.getMaxY(indexx) > this.getMinY(indexx), "node has inverted Y levels");
            LodUtil.assertTrue(lower == 65535 || this.getMinY(indexx) == this.getMaxY(lower), "node does not touch its lower neighbor");
            sizeWithAir++;
            if (this.isIndexVisible(indexx)) {
               sizeWithoutAir++;
            }
         }

         LodUtil.assertTrue(sizeWithAir == this.getSizeWithAir() && sizeWithoutAir == this.getSizeWithoutAir(), "node count does not match size");
      }
   }

   public void remove(int index) {
      int lower = this.getLower(index);
      int higher = this.getHigher(index);
      int smaller = this.getSmaller(index);
      int bigger = this.getBigger(index);
      int alpha = this.getAlpha(index);
      if (lower != 65535) {
         this.setHigher(lower, higher);
      } else {
         this.setLowest(higher);
      }

      if (higher != 65535) {
         this.setLower(higher, lower);
      } else {
         this.setHighest(lower);
      }

      if (smaller != 65535) {
         this.setBigger(smaller, bigger);
      } else {
         this.setSmallest(bigger);
      }

      if (bigger != 65535) {
         this.setSmaller(bigger, smaller);
      } else {
         this.setBiggest(smaller);
      }

      this.setData(index, 0L);
      this.links.set(index, -1L);
      this.sizeWithAir--;
      if (isAlphaVisible(alpha)) {
         this.sizeWithoutAir--;
      }
   }

   @VisibleForTesting
   public void sortBySizeAndReLink() {
      if (this.getSizeWithAir() > 1) {
         LongArrayList datas = this.data;
         int writeIndex = 0;

         for (int readIndex = this.getLowest(); readIndex != 65535; readIndex = this.getHigher(readIndex)) {
            if (datas.getLong(readIndex) != 0L) {
               this.setSortingIndex(writeIndex++, readIndex);
            }
         }

         this.sortBySize(writeIndex);

         for (int index = 1; index < writeIndex; index++) {
            int smaller = this.getSortingIndex(index - 1);
            int bigger = this.getSortingIndex(index);
            this.setSmaller(bigger, smaller);
            this.setBigger(smaller, bigger);
         }

         if (writeIndex == 0) {
            writeIndex = 1;
         }

         this.smallest = this.sortingArray.getShort(0);
         this.biggest = this.sortingArray.getShort(writeIndex - 1);
         this.setSmaller(this.getSmallest(), 65535);
         this.setBigger(this.getBiggest(), 65535);
      }
   }

   @VisibleForTesting
   public void sortBySize(int size) {
      it.unimi.dsi.fastutil.Arrays.quickSort(0, size, this::sortBySizeComparator, this::sortBySizeSwapper);
   }

   private int sortBySizeComparator(int index1, int index2) {
      return Integer.compare(this.getSize(this.getSortingIndex(index1)), this.getSize(this.getSortingIndex(index2)));
   }

   private void sortBySizeSwapper(int index1, int index2) {
      ShortArrays.swap(this.sortingArray.elements(), index1, index2);
   }

   @VisibleForTesting
   public void sortByPosition(int size) {
      it.unimi.dsi.fastutil.Arrays.quickSort(0, size, this::sortByPositionComparator, this::sortByPositionSwapper);
   }

   private int sortByPositionComparator(int index1, int index2) {
      return Integer.compare(this.getMinY(this.getSortingIndex(index1)), this.getMinY(this.getSortingIndex(index2)));
   }

   private void sortByPositionSwapper(int index1, int index2) {
      ShortArrays.swap(this.sortingArray.elements(), index1, index2);
   }

   public void resortSize(int smaller) {
      int bigger = this.getBigger(smaller);
      if (bigger != 65535 && this.getSize(smaller) > this.getSize(bigger)) {
         int smallest = this.getSmaller(smaller);
         if (smallest != 65535) {
            this.setBigger(smallest, bigger);
         } else {
            this.setSmallest(bigger);
         }

         this.setSmaller(bigger, smallest);

         do {
            bigger = this.getBigger(bigger);
         } while (bigger != 65535 && this.getSize(smaller) > this.getSize(bigger));

         this.setSmaller(smaller, bigger != 65535 ? this.getSmaller(bigger) : this.getBiggest());
         this.setBigger(smaller, bigger);
         if (bigger != 65535) {
            this.setSmaller(bigger, smaller);
         } else {
            this.setBiggest(smaller);
         }

         smallest = this.getSmaller(smaller);
         if (smallest != 65535) {
            this.setBigger(smallest, smaller);
         } else {
            this.setSmallest(smaller);
         }
      }
   }

   private int tryMergeStep1(int current, boolean fastPath) {
      int result = fastPath ? this.getSmaller(current) : this.getBigger(current);
      int higher = this.getHigher(current);
      int lower = this.getLower(current);
      int toExtendDownwards;
      int toRemove;
      if (higher != 65535 && this.getAlpha(higher) == this.getAlpha(current)) {
         if (lower == 65535 || this.getAlpha(lower) != this.getAlpha(current)) {
            toExtendDownwards = higher;
            toRemove = current;
         } else if (this.getSize(higher) <= this.getSize(lower)) {
            toExtendDownwards = higher;
            toRemove = current;
         } else {
            toExtendDownwards = current;
            toRemove = lower;
         }
      } else {
         if (lower == 65535 || this.getAlpha(lower) != this.getAlpha(current)) {
            return result;
         }

         toExtendDownwards = current;
         toRemove = lower;
      }

      if (result == toRemove) {
         result = this.getSmaller(result);
      }

      this.setMinY(toExtendDownwards, this.getMinY(toRemove));
      if (!fastPath) {
         this.resortSize(toExtendDownwards);
      }

      this.remove(toRemove);
      return fastPath ? result : this.getSmallest();
   }

   private int lowerNode(int size) {
      for (int node = this.getSmallest(); node != 65535; node = this.getBigger(node)) {
         if (this.getSize(node) >= size) {
            return this.getSmaller(node);
         }
      }

      return this.getBiggest();
   }

   private boolean mergeVerySmallConnectedSegments(int target) {
      for (int specialCase = 1; specialCase <= 2; specialCase++) {
         for (int current = this.lowerNode(specialCase + 1); current != 65535; current = this.tryMergeStep1(current, true)) {
            if (this.getSizeWithoutAir() <= target) {
               this.sortBySizeAndReLink();
               return true;
            }
         }

         this.sortBySizeAndReLink();
      }

      return false;
   }

   private boolean mergeConnectedSegments(int target) {
      for (int current = this.getSmallest(); current != 65535; current = this.tryMergeStep1(current, false)) {
         if (this.getSizeWithoutAir() <= target) {
            return true;
         }
      }

      return false;
   }

   private boolean removeLeastImportantSegments(int target) {
      int center = this.getSmallest();

      while (center != 65535) {
         if (this.getSizeWithoutAir() <= target) {
            return true;
         }

         int lower = this.getLower(center);
         int higher = this.getHigher(center);
         if (lower != 65535 && higher != 65535 && this.getAlpha(lower) == this.getAlpha(higher)) {
            this.setMinY(higher, this.getMinY(lower));
            this.resortSize(higher);
            this.remove(lower);
            this.remove(center);
            center = this.getSmallest();
         } else {
            center = this.getBigger(center);
         }
      }

      return false;
   }

   private void forceBottomToMerge(int target) {
      label28:
      for (int lowest = this.getLowest(); lowest != 65535; lowest = this.getLowest()) {
         if (this.getSizeWithoutAir() <= target) {
            return;
         }

         int lowY = this.getMinY(lowest);

         for (int higher = this.getHigher(lowest); higher != 65535; higher = this.getHigher(higher)) {
            if (this.isIndexVisible(higher)) {
               this.setMinY(higher, lowY);
               this.resortSize(higher);
               this.remove(lowest);
               continue label28;
            }

            this.remove(lowest);
            lowest = higher;
         }

         this.setLowest(65535);
         this.setHighest(65535);
         this.setSmallest(65535);
         this.setBiggest(65535);
         this.setSizeWithAir(0);
         this.setSizeWithoutAir(0);
         return;
      }
   }

   public static long reduceToOne(ColumnRenderView view) {
      int size = view.size;
      if (size <= 0) {
         return 0L;
      } else {
         for (int index = 0; index < size; index++) {
            long dataPoint = view.get(index);
            if (isDataVisible(dataPoint)) {
               long highestDataPoint = dataPoint;

               long lowestDataPoint;
               for (lowestDataPoint = dataPoint; index < size; index++) {
                  dataPoint = view.get(index);
                  if (isDataVisible(dataPoint)) {
                     int yMax = RenderDataPointUtil.getYMax(dataPoint);
                     int yMin = RenderDataPointUtil.getYMin(dataPoint);
                     if (yMax > RenderDataPointUtil.getYMax(highestDataPoint)) {
                        highestDataPoint = dataPoint;
                     } else if (yMin < RenderDataPointUtil.getYMin(lowestDataPoint)) {
                        lowestDataPoint = dataPoint;
                     }
                  }
               }

               return highestDataPoint & -1048321L | RenderDataPointUtil.getYMin(lowestDataPoint) << 8;
            }
         }

         return 0L;
      }
   }

   public void copyTo(ColumnRenderView view) {
      int writeIndex = 0;

      for (int node = this.getHighest(); node != 65535; node = this.getLower(node)) {
         if (this.isIndexVisible(node)) {
            view.set(writeIndex++, this.getData(node));
         }
      }

      if (writeIndex == 0) {
         view.set(writeIndex++, 0L);
      }

      for (int size = view.size; writeIndex < size; writeIndex++) {
         view.set(writeIndex, 0L);
      }
   }

   public int getSmallest() {
      return Short.toUnsignedInt(this.smallest);
   }

   public int getBiggest() {
      return Short.toUnsignedInt(this.biggest);
   }

   public int getLowest() {
      return Short.toUnsignedInt(this.lowest);
   }

   public int getHighest() {
      return Short.toUnsignedInt(this.highest);
   }

   public int getSizeWithAir() {
      return Short.toUnsignedInt(this.sizeWithAir);
   }

   public int getSizeWithoutAir() {
      return Short.toUnsignedInt(this.sizeWithoutAir);
   }

   public int getSortingIndex(int index) {
      return Short.toUnsignedInt(this.sortingArray.getShort(index));
   }

   public int getLower(int index) {
      return (int)(this.links.getLong(index) >>> 0) & 65535;
   }

   public int getHigher(int index) {
      return (int)(this.links.getLong(index) >>> 16) & 65535;
   }

   public int getSmaller(int index) {
      return (int)(this.links.getLong(index) >>> 32) & 65535;
   }

   public int getBigger(int index) {
      return (int)(this.links.getLong(index) >>> 48) & 65535;
   }

   public long getData(int index) {
      return this.data.getLong(index);
   }

   public int getMinY(int index) {
      return RenderDataPointUtil.getYMin(this.getData(index));
   }

   public int getMaxY(int index) {
      return RenderDataPointUtil.getYMax(this.getData(index));
   }

   public int getSize(int index) {
      long data = this.getData(index);
      return RenderDataPointUtil.getYMax(data) - RenderDataPointUtil.getYMin(data);
   }

   public int getRed(int index) {
      return RenderDataPointUtil.getRed(this.getData(index));
   }

   public int getGreen(int index) {
      return RenderDataPointUtil.getGreen(this.getData(index));
   }

   public int getBlue(int index) {
      return RenderDataPointUtil.getBlue(this.getData(index));
   }

   public int getAlpha(int index) {
      return RenderDataPointUtil.getAlpha(this.getData(index));
   }

   public int getBlockLight(int index) {
      return RenderDataPointUtil.getLightBlock(this.getData(index));
   }

   public int getSkyLight(int index) {
      return RenderDataPointUtil.getLightSky(this.getData(index));
   }

   public void setSmallest(int smallest) {
      this.smallest = (short)smallest;
   }

   public void setBiggest(int biggest) {
      this.biggest = (short)biggest;
   }

   public void setLowest(int lowest) {
      this.lowest = (short)lowest;
   }

   public void setHighest(int highest) {
      this.highest = (short)highest;
   }

   public void setSizeWithAir(int sizeWithAir) {
      this.sizeWithAir = (short)sizeWithAir;
   }

   public void setSizeWithoutAir(int sizeWithoutAir) {
      this.sizeWithoutAir = (short)sizeWithoutAir;
   }

   public void setSortingIndex(int index, int to) {
      this.sortingArray.set(index, (short)to);
   }

   public void setLower(int index, int lowerIndex) {
      this.links.set(index, this.links.getLong(index) & -65536L | (long)(lowerIndex & 65535) << 0);
   }

   public void setHigher(int index, int higherIndex) {
      this.links.set(index, this.links.getLong(index) & -4294901761L | (long)(higherIndex & 65535) << 16);
   }

   public void setSmaller(int index, int smallerIndex) {
      this.links.set(index, this.links.getLong(index) & -281470681743361L | (long)(smallerIndex & 65535) << 32);
   }

   public void setBigger(int index, int biggerIndex) {
      this.links.set(index, this.links.getLong(index) & 281474976710655L | (long)(biggerIndex & 65535) << 48);
   }

   public void setData(int index, long data) {
      this.data.set(index, data);
   }

   public void setMinY(int index, int minY) {
      this.data.set(index, this.data.getLong(index) & -1048321L | (minY & 4095L) << 8);
   }

   public void setMaxY(int index, int maxY) {
      this.data.set(index, this.data.getLong(index) & -4293918721L | (maxY & 4095L) << 20);
   }

   public void setRed(int index, int red) {
      this.data.set(index, this.data.getLong(index) & -71776119061217281L | (red & 255L) << 48);
   }

   public void setGreen(int index, int green) {
      this.data.set(index, this.data.getLong(index) & -280375465082881L | (green & 255L) << 40);
   }

   public void setBlue(int index, int blue) {
      this.data.set(index, this.data.getLong(index) & -1095216660481L | (blue & 255L) << 32);
   }

   public void setAlpha(int index, int alpha) {
      alpha >>>= 4;
      this.data.set(index, this.data.getLong(index) & -1080863910568919041L | (alpha & 15L) << 56);
   }

   public void setBlockLight(int index, int blockLight) {
      this.data.set(index, this.data.getLong(index) & -241L | (blockLight & 15L) << 4);
   }

   public void setSkyLight(int index, int skyLight) {
      this.data.set(index, this.data.getLong(index) & -16L | (skyLight & 15L) << 0);
   }

   public boolean isIndexVisible(int index) {
      return isDataVisible(this.getData(index));
   }

   public static boolean isDataVisible(long data) {
      return isAlphaVisible(RenderDataPointUtil.getAlpha(data));
   }

   public static boolean isAlphaVisible(int alpha) {
      return alpha >= 16;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(this.sizeWithAir << 8).append("lowest to highest:");

      for (int index = this.lowest; index != 65535; index = this.getHigher(index)) {
         builder.append('\n').append(RenderDataPointUtil.toString(this.getData(index)));
      }

      builder.append("\nsmallest to biggest:");

      for (int index = this.smallest; index != 65535; index = this.getBigger(index)) {
         builder.append('\n').append(RenderDataPointUtil.toString(this.getData(index)));
      }

      return builder.toString();
   }
}
