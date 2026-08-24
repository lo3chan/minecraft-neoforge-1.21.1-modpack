package com.seibel.distanthorizons.core.dataObjects.render;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.render.columnViews.ColumnRenderView;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.RenderDataPointUtil;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.AbstractPhantomArrayList;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;

public class ColumnRenderSource extends AbstractPhantomArrayList {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int WIDTH = 64;
   public static final int MAX_TEXTURE_PALETTE_SIZE = 256;
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("Render Source");
   public int maxVerticalSliceCount;
   public long pos;
   public int yOffset;
   public final LongArrayList renderDataContainer;
   public final ByteArrayList textureSetPaletteIndices;
   public final ShortArrayList texturePalette = new ShortArrayList();
   private boolean isEmpty = true;

   public static ColumnRenderSource createEmpty(long pos, int maxVertSliceCount, int yOffset) {
      return new ColumnRenderSource(pos, maxVertSliceCount, yOffset);
   }

   private ColumnRenderSource(long pos, int maxVertSliceCount, int yOffset) {
      super(ARRAY_LIST_POOL, 1, 0, 1, 0, 0);
      this.pos = pos;
      this.yOffset = yOffset;
      this.maxVerticalSliceCount = maxVertSliceCount;
      int maxDatapointCount = 4096 * this.maxVerticalSliceCount;
      this.renderDataContainer = this.pooledArraysCheckout.getLongArray(0, maxDatapointCount);
      int textureIndexCount = texturedLodsEnabledAtDetailLevel(this.getDataDetailLevel()) ? maxDatapointCount : 0;
      this.textureSetPaletteIndices = this.pooledArraysCheckout.getByteArray(0, textureIndexCount);
      this.texturePalette.add((short)0);
   }

   private static boolean texturedLodsEnabledAtDetailLevel(byte dataDetailLevel) {
      return Config.Client.Advanced.Graphics.Texture.enableTexturedLods.get()
         && dataDetailLevel <= Config.Client.Advanced.Graphics.Texture.maxTexturedLodDetailLevel.get();
   }

   public long getDataPoint(int posX, int posZ, int verticalIndex) {
      return this.renderDataContainer.getLong(this.getDatapointIndex(posX, posZ, verticalIndex));
   }

   public void populateColumnView(ColumnRenderView view, int posX, int posZ) throws IllegalArgumentException {
      int offset = this.getDatapointIndex(posX, posZ, 0);
      if (offset >= this.renderDataContainer.size()) {
         throw new IllegalArgumentException(
            "Column View offset ["
               + offset
               + "] greater than parent render data container ["
               + DhSectionPos.toString(this.pos)
               + "] size ["
               + this.renderDataContainer.size()
               + "]."
         );
      } else if (posX >= 0 && posX < 64 && posZ >= 0 && posZ < 64) {
         view.populate(this.renderDataContainer, this.maxVerticalSliceCount, offset, this.maxVerticalSliceCount);
      } else {
         throw new IllegalArgumentException("Column View pos outside valid range [" + posX + "," + posZ + "].");
      }
   }

   public boolean hasTextureSetIds() {
      return !this.textureSetPaletteIndices.isEmpty();
   }

   public short getTextureSetId(int posX, int posZ, int verticalIndex) {
      if (!this.hasTextureSetIds()) {
         return 0;
      } else {
         int paletteIndex = this.textureSetPaletteIndices.getByte(this.getDatapointIndex(posX, posZ, verticalIndex)) & 255;
         return this.texturePalette.getShort(paletteIndex);
      }
   }

   public void setTextureSetId(int posX, int posZ, int verticalIndex, short textureSetId) {
      if (this.hasTextureSetIds()) {
         this.textureSetPaletteIndices.set(this.getDatapointIndex(posX, posZ, verticalIndex), this.getOrAddPaletteIndex(textureSetId));
      }
   }

   private byte getOrAddPaletteIndex(short textureSetId) {
      int paletteSize = this.texturePalette.size();

      for (int i = 0; i < paletteSize; i++) {
         if (this.texturePalette.getShort(i) == textureSetId) {
            return (byte)i;
         }
      }

      if (paletteSize >= 256) {
         return 0;
      } else {
         this.texturePalette.add(textureSetId);
         return (byte)paletteSize;
      }
   }

   public byte getDataDetailLevel() {
      return (byte)(DhSectionPos.getDetailLevel(this.pos) - 6);
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public void markNotEmpty() {
      this.isEmpty = false;
   }

   public boolean hasNonVoidDataPoints() {
      if (this.isEmpty) {
         return false;
      } else {
         ColumnRenderView columnView = ColumnRenderView.getPooled();

         boolean var7;
         label69: {
            try {
               for (int x = 0; x < 64; x++) {
                  for (int z = 0; z < 64; z++) {
                     this.populateColumnView(columnView, x, z);

                     for (int i = 0; i < columnView.size; i++) {
                        long dataPoint = columnView.get(i);
                        if (!RenderDataPointUtil.hasZeroHeight(dataPoint)) {
                           var7 = true;
                           break label69;
                        }
                     }
                  }
               }
            } catch (Throwable var9) {
               if (columnView != null) {
                  try {
                     columnView.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (columnView != null) {
               columnView.close();
            }

            return false;
         }

         if (columnView != null) {
            columnView.close();
         }

         return var7;
      }
   }

   private int getDatapointIndex(int posX, int posZ, int verticalIndex) {
      return posX * 64 * this.maxVerticalSliceCount + posZ * this.maxVerticalSliceCount + verticalIndex;
   }

   @Override
   public String toString() {
      String LINE_DELIMITER = "\n";
      String DATA_DELIMITER = " ";
      String SUBDATA_DELIMITER = ",";
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(DhSectionPos.toString(this.pos));
      stringBuilder.append(LINE_DELIMITER);
      int size = 1;

      for (int z = 0; z < size; z++) {
         for (int x = 0; x < size; x++) {
            for (int y = 0; y < this.maxVerticalSliceCount; y++) {
               stringBuilder.append(Long.toHexString(this.getDataPoint(x, z, y)));
               if (y != this.maxVerticalSliceCount - 1) {
                  stringBuilder.append(SUBDATA_DELIMITER);
               }
            }

            if (x != size - 1) {
               stringBuilder.append(DATA_DELIMITER);
            }
         }

         if (z != size - 1) {
            stringBuilder.append(LINE_DELIMITER);
         }
      }

      return stringBuilder.toString();
   }
}
