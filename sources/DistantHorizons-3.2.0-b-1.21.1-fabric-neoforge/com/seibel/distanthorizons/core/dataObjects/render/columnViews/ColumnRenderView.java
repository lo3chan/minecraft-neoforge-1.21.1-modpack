package com.seibel.distanthorizons.core.dataObjects.render.columnViews;

import com.seibel.distanthorizons.core.util.RenderDataPointReducingList;
import com.seibel.distanthorizons.core.util.RenderDataPointUtil;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ColumnRenderView implements AutoCloseable {
   private static final ConcurrentLinkedQueue<ColumnRenderView> POOL = new ConcurrentLinkedQueue<>();
   public LongArrayList data;
   public int size;
   public int maxVerticalSliceCount;
   public int offset;

   private ColumnRenderView() {
   }

   public static ColumnRenderView getPooled() {
      return getPooled(null, 0, 0, 0);
   }

   public static ColumnRenderView getPooled(LongArrayList data, int size, int offset, int maxVerticalSliceCount) throws IllegalArgumentException {
      ColumnRenderView view = POOL.poll();
      if (view == null) {
         view = new ColumnRenderView();
      }

      if (data != null) {
         view.populate(data, size, offset, maxVerticalSliceCount);
      }

      return view;
   }

   public void populate(LongArrayList data, int size, int offset, int maxVerticalSliceCount) throws IllegalArgumentException {
      this.data = data;
      this.size = size;
      this.offset = offset;
      this.maxVerticalSliceCount = maxVerticalSliceCount;
      if (this.data.size() < this.offset) {
         throw new IllegalArgumentException("data size [" + this.data.size() + "] is shorter than offset [" + this.offset + "].");
      }
   }

   public void clear() {
      this.data = null;
      this.size = 0;
      this.offset = 0;
      this.maxVerticalSliceCount = 0;
   }

   public long get(int index) {
      try {
         return this.data.getLong(index + this.offset);
      } catch (IndexOutOfBoundsException var3) {
         throw new ConcurrentModificationException(
            "Potential concurrent modification detected. Make sure the parent ColumnRenderSource isn't being closed before the ColumnRenderView processing is complete.",
            var3
         );
      }
   }

   public void set(int index, long value) {
      this.data.set(index + this.offset, value);
   }

   public void fill(long value) {
      Arrays.fill(this.data.elements(), this.offset, this.offset + this.size, value);
   }

   public ColumnRenderView subView(int dataIndexStart, int dataCount) {
      return getPooled(this.data, dataCount * this.maxVerticalSliceCount, this.offset + dataIndexStart * this.maxVerticalSliceCount, this.maxVerticalSliceCount);
   }

   public int subViewCount() {
      return this.maxVerticalSliceCount != 0 ? this.size / this.maxVerticalSliceCount : 0;
   }

   public void changeVerticalSizeFrom(ColumnRenderView source, RenderDataPointReducingList reducingList) {
      if (this.subViewCount() != source.subViewCount()) {
         throw new IllegalArgumentException("Cannot copy and resize to views with different dataCounts");
      } else {
         if (this.maxVerticalSliceCount >= source.maxVerticalSliceCount) {
            this.copyFrom(source, 0);
         } else {
            for (int i = 0; i < this.subViewCount(); i++) {
               ColumnRenderView sourceSubView = source.subView(i, 1);

               try {
                  ColumnRenderView thisSubView = this.subView(i, 1);

                  try {
                     mergeMultiData(sourceSubView, reducingList, thisSubView);
                  } catch (Throwable var10) {
                     if (thisSubView != null) {
                        try {
                           thisSubView.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (thisSubView != null) {
                     thisSubView.close();
                  }
               } catch (Throwable var11) {
                  if (sourceSubView != null) {
                     try {
                        sourceSubView.close();
                     } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                     }
                  }

                  throw var11;
               }

               if (sourceSubView != null) {
                  sourceSubView.close();
               }
            }
         }
      }
   }

   private void copyFrom(ColumnRenderView source, int outputDataIndexOffset) {
      if (source.maxVerticalSliceCount > this.maxVerticalSliceCount) {
         throw new IllegalArgumentException("source verticalSize must be <= self's verticalSize to copy");
      } else if (source.subViewCount() + outputDataIndexOffset > this.subViewCount()) {
         throw new IllegalArgumentException("dataIndexStart + source.dataCount() must be <= self.dataCount() to copy");
      } else {
         if (source.maxVerticalSliceCount != this.maxVerticalSliceCount) {
            for (int i = 0; i < source.subViewCount(); i++) {
               int outputOffset = this.offset + outputDataIndexOffset * this.maxVerticalSliceCount + i * this.maxVerticalSliceCount;
               ColumnRenderView subView = source.subView(i, 1);

               try {
                  subView.copyTo(this.data.elements(), outputOffset, source.maxVerticalSliceCount);
                  Arrays.fill(this.data.elements(), outputOffset + source.maxVerticalSliceCount, outputOffset + this.maxVerticalSliceCount, 0L);
               } catch (Throwable var9) {
                  if (subView != null) {
                     try {
                        subView.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (subView != null) {
                  subView.close();
               }
            }
         } else {
            source.copyTo(this.data.elements(), this.offset + outputDataIndexOffset * this.maxVerticalSliceCount, source.size);
         }
      }
   }

   private void copyTo(long[] target, int offset, int size) {
      System.arraycopy(this.data.elements(), this.offset, target, offset, size);
   }

   private static void mergeMultiData(ColumnRenderView sourceData, RenderDataPointReducingList reducingList, ColumnRenderView output) {
      int target = output.maxVerticalSliceCount;
      if (target <= 0) {
         output.fill(0L);
      } else if (target == 1) {
         output.set(0, RenderDataPointReducingList.reduceToOne(sourceData));
         int index = 1;

         for (int size = output.size; index < size; index++) {
            output.set(index, 0L);
         }
      } else {
         reducingList.populate(sourceData);
         reducingList.reduce(output.maxVerticalSliceCount);
         reducingList.copyTo(output);
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("S:").append(this.size);
      sb.append(" V:").append(this.maxVerticalSliceCount);
      sb.append(" O:").append(this.offset);
      sb.append(" [");

      for (int i = 0; i < this.size; i++) {
         sb.append(RenderDataPointUtil.toString(this.data.getLong(this.offset + i)));
         if (i < this.size - 1) {
            sb.append(",\n");
         }
      }

      sb.append("]");
      return sb.toString();
   }

   @Override
   public void close() {
      POOL.add(this);
   }
}
