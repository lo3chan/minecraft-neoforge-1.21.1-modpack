package com.seibel.distanthorizons.core.dataObjects.render.bufferBuilding;

import com.seibel.distanthorizons.api.enums.config.EDhApiGrassSideRendering;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiBlockMaterial;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiDebugRendering;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.ByteBufferCheckoutWrapper;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class LodQuadBuilder implements AutoCloseable {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
   private static final ThreadLocal<LodQuadBuilder> THREAD_LOCAL = ThreadLocal.withInitial(LodQuadBuilder::new);
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("LodQuadBuilder");
   public static final int BYTES_PER_VERTEX = 16;
   public static final int BYTES_PER_QUAD = 64;
   public static final int[][][] DIRECTION_VERTEX_IBO_QUAD = new int[][][]{
      {{1, 0}, {1, 1}, {0, 1}, {0, 0}},
      {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
      {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
      {{1, 0}, {1, 1}, {0, 1}, {0, 0}},
      {{0, 0}, {1, 0}, {1, 1}, {0, 1}},
      {{0, 1}, {1, 1}, {1, 0}, {0, 0}}
   };
   private final ArrayList<BufferQuad>[] opaqueQuads = new ArrayList[6];
   private final ArrayList<BufferQuad>[] transparentQuads = new ArrayList[6];
   @Nullable
   private short[] currentFaceTileIds = null;
   private final ArrayList<BufferQuad> bufferQuadCacheList = new ArrayList<>();
   private boolean doTransparency;
   private IClientLevelWrapper clientLevelWrapper;
   private EDhApiDebugRendering debugRenderingMode;
   private EDhApiGrassSideRendering grassSideRenderingMode;
   private int premergeCount = 0;
   private static int maxBufferByteSize = -1;

   private LodQuadBuilder() {
      for (int i = 0; i < 6; i++) {
         this.opaqueQuads[i] = new ArrayList<>();
         this.transparentQuads[i] = new ArrayList<>();
      }
   }

   public static LodQuadBuilder getBuilder(boolean doTransparency, IClientLevelWrapper clientLevelWrapper) {
      LodQuadBuilder builder = THREAD_LOCAL.get();
      builder.set(doTransparency, clientLevelWrapper);
      return builder;
   }

   private void set(boolean doTransparency, IClientLevelWrapper clientLevelWrapper) {
      this.doTransparency = doTransparency;
      this.clientLevelWrapper = clientLevelWrapper;
      this.debugRenderingMode = Config.Client.Advanced.Debugging.debugRenderingColors.get();
      this.grassSideRenderingMode = Config.Client.Advanced.Graphics.Quality.grassSideRendering.get();
      this.premergeCount = 0;
   }

   public void addQuadAdj(
      EDhDirection dir,
      short x,
      short y,
      short z,
      short width,
      short height,
      int color,
      short faceTextureId,
      byte irisBlockMaterialId,
      byte skyLight,
      byte blockLight
   ) {
      if (dir == EDhDirection.DOWN) {
         throw new IllegalArgumentException("addQuadAdj() is only for adj direction! Not UP or Down!");
      } else {
         ArrayList<BufferQuad> quadList;
         if (this.doTransparency && ColorUtil.getAlpha(color) < 255) {
            quadList = this.transparentQuads[dir.faceIndex];
         } else {
            quadList = this.opaqueQuads[dir.faceIndex];
         }

         BufferQuad quad = this.getOrCreateBufferQuad();
         quad.set(x, y, z, width, height, color, faceTextureId, irisBlockMaterialId, skyLight, blockLight, dir);
         if (quadList.isEmpty()
            || !quadList.get(quadList.size() - 1).tryMerge(quad, BufferMergeDirectionEnum.EastWest)
               && !quadList.get(quadList.size() - 1).tryMerge(quad, BufferMergeDirectionEnum.NorthSouthOrUpDown)) {
            quadList.add(quad);
         } else {
            this.premergeCount++;
         }
      }
   }

   public void addQuadUp(
      short minX, short maxY, short minZ, short blockWidth, int color, short faceTextureId, byte irisBlockMaterialId, byte skylight, byte blocklight
   ) {
      boolean isTransparent = this.doTransparency && ColorUtil.getAlpha(color) < 255;
      ArrayList<BufferQuad> quadList = isTransparent ? this.transparentQuads[EDhDirection.UP.faceIndex] : this.opaqueQuads[EDhDirection.UP.faceIndex];
      BufferQuad quad = this.getOrCreateBufferQuad();
      quad.set(minX, maxY, minZ, blockWidth, blockWidth, color, faceTextureId, irisBlockMaterialId, skylight, blocklight, EDhDirection.UP);
      quadList.add(quad);
   }

   public void addQuadDown(
      short x, short y, short z, short blockWidth, int color, short faceTextureId, byte irisBlockMaterialId, byte skylight, byte blocklight
   ) {
      ArrayList<BufferQuad> quadArray = this.doTransparency && ColorUtil.getAlpha(color) < 255
         ? this.transparentQuads[EDhDirection.DOWN.faceIndex]
         : this.opaqueQuads[EDhDirection.DOWN.faceIndex];
      BufferQuad quad = this.getOrCreateBufferQuad();
      quad.set(x, y, z, blockWidth, blockWidth, color, faceTextureId, irisBlockMaterialId, skylight, blocklight, EDhDirection.DOWN);
      quadArray.add(quad);
   }

   public void mergeQuads() {
      long mergeCount = 0L;
      long preQuadsCount = this.getCurrentOpaqueQuadsCount() + this.getCurrentTransparentQuadsCount();
      if (preQuadsCount > 1L) {
         for (int directionIndex = 0; directionIndex < 6; directionIndex++) {
            mergeCount += mergeQuadsInternal(this.opaqueQuads, directionIndex, BufferMergeDirectionEnum.EastWest);
            if (this.doTransparency) {
               mergeCount += mergeQuadsInternal(this.transparentQuads, directionIndex, BufferMergeDirectionEnum.EastWest);
            }

            if (directionIndex == EDhDirection.UP.faceIndex || directionIndex == EDhDirection.DOWN.faceIndex) {
               mergeCount += mergeQuadsInternal(this.opaqueQuads, directionIndex, BufferMergeDirectionEnum.NorthSouthOrUpDown);
               if (this.doTransparency) {
                  mergeCount += mergeQuadsInternal(this.transparentQuads, directionIndex, BufferMergeDirectionEnum.NorthSouthOrUpDown);
               }
            }
         }
      }
   }

   private static long mergeQuadsInternal(ArrayList<BufferQuad>[] list, int directionIndex, BufferMergeDirectionEnum mergeDirection) {
      if (list[directionIndex].size() <= 1) {
         return 0L;
      } else {
         list[directionIndex].sort((objOne, objTwo) -> objOne.compare(objTwo, mergeDirection));
         long mergeCount = 0L;
         ListIterator<BufferQuad> iter = list[directionIndex].listIterator();
         BufferQuad currentQuad = iter.next();

         while (iter.hasNext()) {
            BufferQuad nextQuad = iter.next();
            if (currentQuad.tryMerge(nextQuad, mergeDirection)) {
               mergeCount++;
               iter.set(null);
            } else {
               currentQuad = nextQuad;
            }
         }

         list[directionIndex].removeIf(Objects::isNull);
         return mergeCount;
      }
   }

   public ArrayList<ByteBuffer> makeOpaqueVertexBuffers(PhantomArrayListCheckout checkout) {
      return this.makeVertexBuffers(checkout, this.opaqueQuads);
   }

   public ArrayList<ByteBuffer> makeTransparentVertexBuffers(PhantomArrayListCheckout checkout) {
      return this.makeVertexBuffers(checkout, this.transparentQuads);
   }

   private ArrayList<ByteBuffer> makeVertexBuffers(PhantomArrayListCheckout checkout, ArrayList<BufferQuad>[] quadList) {
      ArrayList<ByteBuffer> byteBufferList = new ArrayList<>(3);
      int byteBufferSize = getMaxBufferByteSize();
      ByteBuffer buffer = null;

      for (int directionIndex = 0; directionIndex < 6; directionIndex++) {
         if (!quadList[directionIndex].isEmpty()) {
            for (int quadIndex = 0; quadIndex < quadList[directionIndex].size(); quadIndex++) {
               if (buffer == null || buffer.remaining() < 64) {
                  if (byteBufferList.size() + 1 > checkout.getByteBufferWrapperCount()) {
                     ByteBufferCheckoutWrapper wrapper = new ByteBufferCheckoutWrapper(byteBufferSize);
                     checkout.addByteBufferWrapper(wrapper);
                     buffer = wrapper.buffer;
                  } else {
                     buffer = checkout.getByteBuffer(byteBufferList.size(), byteBufferSize);
                  }

                  byteBufferList.add(buffer);
               }

               this.putQuad(buffer, quadList[directionIndex].get(quadIndex));
            }
         }
      }

      for (int i = 0; i < byteBufferList.size(); i++) {
         buffer = byteBufferList.get(i);
         ((Buffer)buffer).limit(buffer.position());
         ((Buffer)buffer).rewind();
      }

      return byteBufferList;
   }

   private void putQuad(ByteBuffer bb, BufferQuad quad) {
      int[][] quadBase = DIRECTION_VERTEX_IBO_QUAD[quad.direction.faceIndex];
      short widthEastWest = quad.widthEastWest;
      short widthNorthSouth = quad.widthNorthSouthOrHeight;
      byte normalIndex = (byte)quad.direction.faceIndex;
      EDhDirection.Axis axis = quad.direction.axis;

      for (int i = 0; i < quadBase.length; i++) {
         short dx;
         short dy;
         short dz;
         int mx;
         int my;
         int mz;
         switch (axis) {
            case X:
               dx = 0;
               dy = quadBase[i][1] == 1 ? widthNorthSouth : 0;
               dz = quadBase[i][0] == 1 ? widthEastWest : 0;
               mx = 0;
               my = quadBase[i][1] == 1 ? 1 : -1;
               mz = quadBase[i][0] == 1 ? 1 : -1;
               break;
            case Y:
               dx = quadBase[i][0] == 1 ? widthEastWest : 0;
               dy = 0;
               dz = quadBase[i][1] == 1 ? widthNorthSouth : 0;
               mx = quadBase[i][0] == 1 ? 1 : -1;
               my = 0;
               mz = quadBase[i][1] == 1 ? 1 : -1;
               break;
            case Z:
               dx = quadBase[i][0] == 1 ? widthEastWest : 0;
               dy = quadBase[i][1] == 1 ? widthNorthSouth : 0;
               dz = 0;
               mx = quadBase[i][0] == 1 ? 1 : -1;
               my = quadBase[i][1] == 1 ? 1 : -1;
               mz = 0;
               break;
            default:
               throw new IllegalArgumentException("Invalid Axis enum: " + axis);
         }

         int color = quad.color;
         if (quad.irisBlockMaterialId == EDhApiBlockMaterial.GRASS.index
            && this.debugRenderingMode == EDhApiDebugRendering.OFF
            && this.grassSideRenderingMode != EDhApiGrassSideRendering.AS_GRASS
            && (quad.direction.axis.isHorizontal() || quad.direction == EDhDirection.DOWN)
            && (
               this.grassSideRenderingMode == EDhApiGrassSideRendering.AS_DIRT
                  || this.grassSideRenderingMode == EDhApiGrassSideRendering.FADE_TO_DIRT && quadBase[i][1] == 0
                  || quad.direction == EDhDirection.DOWN
            )) {
            color = this.clientLevelWrapper.getDirtBlockColor();
            color = ColorUtil.applyShade(color, this.clientLevelWrapper.getShade(quad.direction));
         }

         this.putVertex(
            bb,
            (short)(quad.x + dx),
            (short)(quad.y + dy),
            (short)(quad.z + dz),
            quad.hasError ? ColorUtil.RED : color,
            quad.hasError ? 0 : normalIndex,
            quad.hasError ? 0 : quad.irisBlockMaterialId,
            quad.hasError ? 15 : quad.skyLight,
            quad.hasError ? 15 : quad.blockLight,
            quad.hasError ? 0 : quad.textureTileId,
            mx,
            my,
            mz
         );
      }
   }

   private void putVertex(
      ByteBuffer bb,
      short x,
      short y,
      short z,
      int color,
      byte normalIndex,
      byte irisBlockMaterialId,
      byte skylight,
      byte blocklight,
      short textureTileId,
      int mx,
      int my,
      int mz
   ) {
      bb.putShort(x);
      bb.putShort(y);
      bb.putShort(z);
      short meta = 0;
      skylight = (byte)(skylight % 16);
      blocklight = (byte)(blocklight % 16);
      meta = (short)(meta | (short)(skylight | blocklight << 4));
      byte mircoOffset = 0;
      if (mx != 0) {
         mircoOffset |= (byte)(mx > 0 ? 1 : 3);
      }

      if (my != 0) {
         mircoOffset |= (byte)(my > 0 ? 4 : 12);
      }

      if (mz != 0) {
         mircoOffset |= (byte)(mz > 0 ? 16 : 48);
      }

      meta = (short)(meta | (short)(mircoOffset << 8));
      bb.putShort(meta);
      mircoOffset = (byte)ColorUtil.getRed(color);
      byte g = (byte)ColorUtil.getGreen(color);
      byte b = (byte)ColorUtil.getBlue(color);
      byte a = this.doTransparency ? (byte)ColorUtil.getAlpha(color) : -1;
      bb.put(mircoOffset);
      bb.put(g);
      bb.put(b);
      bb.put(a);
      bb.put(irisBlockMaterialId);
      bb.put(normalIndex);
      bb.putShort(textureTileId);
   }

   public int getCurrentOpaqueQuadsCount() {
      int i = 0;

      for (ArrayList<BufferQuad> quadList : this.opaqueQuads) {
         i += quadList.size();
      }

      return i;
   }

   public int getCurrentTransparentQuadsCount() {
      if (!this.doTransparency) {
         return 0;
      } else {
         int i = 0;

         for (ArrayList<BufferQuad> quadList : this.transparentQuads) {
            i += quadList.size();
         }

         return i;
      }
   }

   public static int getMaxBufferByteSize() {
      if (maxBufferByteSize != -1) {
         return maxBufferByteSize;
      } else {
         int maxVboByteSize = 2097152;
         int maxQuadsPerBuffer = maxVboByteSize / 64;
         int fullSizedBuffer = maxQuadsPerBuffer * 64;
         maxBufferByteSize = fullSizedBuffer;
         return fullSizedBuffer;
      }
   }

   private BufferQuad getOrCreateBufferQuad() {
      int index = this.bufferQuadCacheList.size() - 1;
      if (index < 0) {
         return new BufferQuad();
      } else {
         BufferQuad quad = this.bufferQuadCacheList.remove(index);
         return quad != null ? quad : new BufferQuad();
      }
   }

   private static void returnQuadsToCache(ArrayList<BufferQuad> quadCache, ArrayList<BufferQuad>[] quadsToReturn) {
      for (int i = 0; i < quadsToReturn.length; i++) {
         for (int j = 0; j < quadsToReturn[i].size(); j++) {
            quadCache.add(quadsToReturn[i].get(j));
         }

         quadsToReturn[i].clear();
      }
   }

   @Override
   public void close() {
      returnQuadsToCache(this.bufferQuadCacheList, this.opaqueQuads);
      returnQuadsToCache(this.bufferQuadCacheList, this.transparentQuads);
   }
}
