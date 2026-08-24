package com.yungnickyoung.minecraft.betterdungeons.world.structure.spider_dungeon.piece;

import com.yungnickyoung.minecraft.betterdungeons.mixin.accessor.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.betterdungeons.module.StructurePieceTypeModule;
import java.util.BitSet;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.Fluids;

@ParametersAreNonnullByDefault
public class SpiderDungeonSmallTunnelPiece extends SpiderDungeonPiece {
   private final BlockPos startPos;
   private BlockPos endPos;
   private float pitch = 0.0F;
   private final float[] yaws = new float[30];
   private static final int LENGTH = 30;
   private static final float X_MINRADIUS = 1.0F;
   private static final float X_MAXRADIUS = 1.5F;
   private static final float Y_MINRADIUS = 2.0F;
   private static final float Y_MAXRADIUS = 2.0F;
   private static final float Z_MINRADIUS = 1.0F;
   private static final float Z_MAXRADIUS = 1.5F;

   public SpiderDungeonSmallTunnelPiece(BlockPos startPos, float initialYaw, int pieceChainLength) {
      super(StructurePieceTypeModule.SMALL_TUNNEL, pieceChainLength, getInitialBoundingBox(startPos));
      this.startPos = new BlockPos(startPos);
      this.endPos = new BlockPos(startPos);
      this.yaws[0] = initialYaw;
   }

   public SpiderDungeonSmallTunnelPiece(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.SMALL_TUNNEL, compoundTag);
      int[] start = compoundTag.getIntArray("startPos");
      int[] end = compoundTag.getIntArray("endPos");
      this.startPos = new BlockPos(start[0], start[1], start[2]);
      this.endPos = new BlockPos(end[0], end[1], end[2]);
      this.pitch = compoundTag.getFloat("pitch");
      ListTag yawNbtList = compoundTag.getList("yawList", 5);

      for (int i = 0; i < 30; i++) {
         this.yaws[i] = yawNbtList.getFloat(i);
      }
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      compoundTag.putIntArray("startPos", new int[]{this.startPos.getX(), this.startPos.getY(), this.startPos.getZ()});
      compoundTag.putIntArray("endPos", new int[]{this.endPos.getX(), this.endPos.getY(), this.endPos.getZ()});
      compoundTag.putFloat("pitch", this.pitch);
      ListTag yawNbtList = new ListTag();

      for (int i = 0; i < 30; i++) {
         yawNbtList.add(FloatTag.valueOf(this.yaws[i]));
      }

      compoundTag.put("yawList", yawNbtList);
   }

   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      this.pitch = randomSource.nextFloat() * 3.1415927F / 4.0F - 0.5235988F;
      float pitchY = Mth.sin(this.pitch);
      float pitchXZ = Mth.cos(this.pitch);
      int minX = 2147483647;
      int maxX = -2147483648;
      int minY = 2147483647;
      int maxY = -2147483648;
      int minZ = 2147483647;
      int maxZ = -2147483648;
      float caveStartX = this.startPos.getX();
      float caveStartY = this.startPos.getY();
      float caveStartZ = this.startPos.getZ();
      caveStartX += Mth.cos(this.yaws[0]) * pitchXZ;
      caveStartY += Mth.sin(pitchY);
      caveStartZ += Mth.sin(this.yaws[0]) * pitchXZ;
      float yawModifier = 0.0F;
      if (caveStartX - 1.5F - 4.0F < minX) {
         minX = (int)caveStartX - 1 - 4;
      }

      if (caveStartX + 1.5F + 4.0F > maxX) {
         maxX = (int)caveStartX + 1 + 4;
      }

      if (caveStartY - 2.0F - 4.0F < minY) {
         minY = (int)caveStartY - 2 - 4;
      }

      if (caveStartY + 2.0F + 4.0F > maxY) {
         maxY = (int)caveStartY + 2 + 4;
      }

      if (caveStartZ - 1.5F - 4.0F < minZ) {
         minZ = (int)caveStartZ - 1 - 4;
      }

      if (caveStartZ + 1.5F + 4.0F > maxZ) {
         maxZ = (int)caveStartZ + 1 + 4;
      }

      for (int i = 1; i < 30; i++) {
         float var20 = yawModifier * 0.75F;
         yawModifier = var20 + randomSource.nextFloat() * randomSource.nextFloat();
         this.yaws[i] = this.yaws[i - 1] + yawModifier * 0.02F;
         caveStartX += Mth.cos(this.yaws[i]) * pitchXZ;
         caveStartY += Mth.sin(pitchY);
         caveStartZ += Mth.sin(this.yaws[i]) * pitchXZ;
         if (caveStartX - 1.5F - 4.0F < minX) {
            minX = (int)caveStartX - 1 - 4;
         }

         if (caveStartX + 1.5F + 4.0F > maxX) {
            maxX = (int)caveStartX + 1 + 4;
         }

         if (caveStartY - 2.0F - 4.0F < minY) {
            minY = (int)caveStartY - 2 - 4;
         }

         if (caveStartY + 2.0F + 4.0F > maxY) {
            maxY = (int)caveStartY + 2 + 4;
         }

         if (caveStartZ - 1.5F - 4.0F < minZ) {
            minZ = (int)caveStartZ - 1 - 4;
         }

         if (caveStartZ + 1.5F + 4.0F > maxZ) {
            maxZ = (int)caveStartZ + 1 + 4;
         }
      }

      ((BoundingBoxAccessor)this.boundingBox).setMinX(minX);
      ((BoundingBoxAccessor)this.boundingBox).setMaxX(maxX);
      ((BoundingBoxAccessor)this.boundingBox).setMinY(minY);
      ((BoundingBoxAccessor)this.boundingBox).setMaxY(maxY);
      ((BoundingBoxAccessor)this.boundingBox).setMinZ(minZ);
      ((BoundingBoxAccessor)this.boundingBox).setMaxZ(maxZ);
      this.endPos = new BlockPos((int)caveStartX, (int)caveStartY, (int)caveStartZ);
      if (randomSource.nextFloat() < 0.8F) {
         StructurePiece eggRoom = new SpiderDungeonEggRoomPiece(this.endPos, this.genDepth + 1);
         structurePieceAccessor.addPiece(eggRoom);
         eggRoom.addChildren(eggRoom, structurePieceAccessor, randomSource);
      }
   }

   public void postProcess(
      WorldGenLevel world,
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      RandomSource randomSource,
      BoundingBox box,
      ChunkPos chunkPos,
      BlockPos blockPos
   ) {
      MutableBlockPos mutable = new MutableBlockPos();
      WorldgenRandom decoRand = new WorldgenRandom(new LegacyRandomSource(0L));
      decoRand.setLargeFeatureSeed(world.getSeed(), this.startPos.getX(), this.startPos.getZ());
      int xBits = 4;
      int zBits = 4;
      int yBits = Mth.ceillog2(world.getMaxBuildHeight() - world.getMinBuildHeight());
      BitSet carvingMask = new BitSet((int)Math.pow(2.0, xBits + zBits + yBits));
      int[] surface = new int[256];

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            mutable.set(chunkPos.getMinBlockX() + x, 1, chunkPos.getMinBlockZ() + z);
            surface[x * 16 + z] = world.getHeight(Types.WORLD_SURFACE_WG, mutable.getX(), mutable.getZ());
         }
      }

      float caveStartX = this.startPos.getX();
      float caveStartY = this.startPos.getY();
      float caveStartZ = this.startPos.getZ();

      for (int i = 0; i < 30; i++) {
         float pitchY = Mth.sin(this.pitch);
         float pitchXZ = Mth.cos(this.pitch);
         float yaw = this.yaws[i];
         caveStartX += Mth.cos(yaw) * pitchXZ;
         caveStartY += Mth.sin(pitchY);
         caveStartZ += Mth.sin(yaw) * pitchXZ;
         float xRadius = Mth.lerp(Mth.sin(i * 3.1415927F / 30.0F), 1.0F, 1.5F);
         float yRadius = Mth.lerp(Mth.sin(i * 3.1415927F / 30.0F), 2.0F, 2.0F);
         float zRadius = Mth.lerp(Mth.sin(i * 3.1415927F / 30.0F), 1.0F, 1.5F);
         int minX = Mth.floor(caveStartX - xRadius) - chunkPos.x * 16 - 1;
         int maxX = Mth.floor(caveStartX + xRadius) - chunkPos.x * 16 + 1;
         int minY = Mth.clamp(Mth.floor(caveStartY - yRadius) - 1, world.getMinBuildHeight(), world.getMaxBuildHeight());
         int maxY = Mth.clamp(Mth.floor(caveStartY + yRadius) + 1, world.getMinBuildHeight(), world.getMaxBuildHeight());
         int minZ = Mth.floor(caveStartZ - zRadius) - chunkPos.z * 16 - 1;
         int maxZ = Mth.floor(caveStartZ + zRadius) - chunkPos.z * 16 + 1;
         minX = Mth.clamp(minX, 0, 15);
         maxX = Mth.clamp(maxX, 0, 15);
         minZ = Mth.clamp(minZ, 0, 15);
         maxZ = Mth.clamp(maxZ, 0, 15);

         for (float x = minX; x <= maxX; x++) {
            int globalX = (int)x + chunkPos.x * 16;
            if (globalX >= chunkPos.getMinBlockX() && globalX <= chunkPos.getMaxBlockX()) {
               float radialXDist = (globalX - caveStartX + 0.5F) / xRadius;

               for (float z = minZ; z <= maxZ; z++) {
                  int globalZ = (int)z + chunkPos.z * 16;
                  if (globalZ >= chunkPos.getMinBlockZ() && globalZ <= chunkPos.getMaxBlockZ()) {
                     float radialZDist = (globalZ - caveStartZ + 0.5F) / zRadius;

                     for (float y = minY; y <= maxY; y++) {
                        int globalY = (int)y;
                        if (globalY > surface[(int)x % 16 * 16 + (int)z % 16]) {
                           break;
                        }

                        float radialYDist = (y - caveStartY - 0.5F) / yRadius;
                        int mask = (int)x | (int)z << 4 | (int)(y - world.getMinBuildHeight()) << 8;
                        float radialDist = radialXDist * radialXDist + radialYDist * radialYDist + radialZDist * radialZDist;
                        if (carvingMask.get(mask) || !(radialDist < 1.0)) {
                           float radialXDistShell = (globalX - caveStartX + 0.5F) / (xRadius + 1.2F);
                           float radialYDistShell = (y - caveStartY - 0.5F) / (yRadius + 1.2F);
                           float radialZDistShell = (globalZ - caveStartZ + 0.5F) / (zRadius + 1.2F);
                           float radialDistShell = radialXDistShell * radialXDistShell
                              + radialYDistShell * radialYDistShell
                              + radialZDistShell * radialZDistShell;
                           if (!carvingMask.get(mask) && radialDistShell < 1.0) {
                              BlockState state = this.getBlock(world, globalX, globalY, globalZ, box);
                              if (!BLOCK_BLACKLIST.contains(state.getBlock())
                                 && (state.isAir() || state.getFluidState().getType() != Fluids.EMPTY || decoRand.nextFloat() < 0.2F)) {
                                 this.placeBlock(world, Blocks.COBBLESTONE.defaultBlockState(), globalX, globalY, globalZ, box);
                              }
                           }
                        } else if (!BLOCK_BLACKLIST.contains(this.getBlock(world, globalX, globalY, globalZ, box).getBlock())) {
                           this.placeBlock(world, Blocks.CAVE_AIR.defaultBlockState(), globalX, globalY, globalZ, box);
                           carvingMask.set(mask);
                        }
                     }
                  }
               }
            }
         }
      }

      this.decorateCave(world, decoRand, chunkPos, box, carvingMask);
   }
}
