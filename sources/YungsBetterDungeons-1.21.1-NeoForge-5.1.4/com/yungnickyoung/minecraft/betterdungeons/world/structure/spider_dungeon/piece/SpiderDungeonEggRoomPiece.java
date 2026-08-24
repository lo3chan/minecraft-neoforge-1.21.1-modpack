package com.yungnickyoung.minecraft.betterdungeons.world.structure.spider_dungeon.piece;

import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.mixin.accessor.BoundingBoxAccessor;
import com.yungnickyoung.minecraft.betterdungeons.module.StructurePieceTypeModule;
import com.yungnickyoung.minecraft.yungsapi.api.world.randomize.BlockStateRandomizer;
import java.util.BitSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
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

public class SpiderDungeonEggRoomPiece extends SpiderDungeonPiece {
   private final BlockPos startPos;
   private float xRadius = 0.0F;
   private float yRadius = 0.0F;
   private float zRadius = 0.0F;
   private static final float X_MINRADIUS = 2.0F;
   private static final float X_MAXRADIUS = 3.0F;
   private static final float Y_MINRADIUS = 2.0F;
   private static final float Y_MAXRADIUS = 3.0F;
   private static final float Z_MINRADIUS = 2.0F;
   private static final float Z_MAXRADIUS = 3.0F;
   private static final BlockStateRandomizer WOOL_SELECTOR = BlockStateRandomizer.from(new BlockState[]{Blocks.WHITE_WOOL.defaultBlockState()});
   private static final BlockStateRandomizer COBWEB_SELECTOR = BlockStateRandomizer.from(new BlockState[]{Blocks.COBWEB.defaultBlockState()});

   public SpiderDungeonEggRoomPiece(BlockPos startPos, int pieceChainLength) {
      super(StructurePieceTypeModule.EGG_ROOM, pieceChainLength, getInitialBoundingBox(startPos));
      this.startPos = new BlockPos(startPos);
   }

   public SpiderDungeonEggRoomPiece(CompoundTag compoundTag) {
      super(StructurePieceTypeModule.EGG_ROOM, compoundTag);
      int[] start = compoundTag.getIntArray("startPos");
      this.startPos = new BlockPos(start[0], start[1], start[2]);
      this.xRadius = compoundTag.getFloat("xRadius");
      this.yRadius = compoundTag.getFloat("yRadius");
      this.zRadius = compoundTag.getFloat("zRadius");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
      compoundTag.putIntArray("startPos", new int[]{this.startPos.getX(), this.startPos.getY(), this.startPos.getZ()});
      compoundTag.putFloat("xRadius", this.xRadius);
      compoundTag.putFloat("yRadius", this.yRadius);
      compoundTag.putFloat("zRadius", this.zRadius);
   }

   public void addChildren(StructurePiece structurePiece, StructurePieceAccessor structurePieceAccessor, RandomSource randomSource) {
      this.xRadius = randomSource.nextFloat() * 1.0F + 2.0F;
      this.yRadius = randomSource.nextFloat() * 1.0F + 2.0F;
      this.zRadius = randomSource.nextFloat() * 1.0F + 2.0F;
      ((BoundingBoxAccessor)this.boundingBox).setMinX(this.startPos.getX() - (int)this.xRadius - 4);
      ((BoundingBoxAccessor)this.boundingBox).setMaxX(this.startPos.getX() + (int)this.xRadius + 4);
      ((BoundingBoxAccessor)this.boundingBox).setMinY(this.startPos.getY() - (int)this.yRadius - 4);
      ((BoundingBoxAccessor)this.boundingBox).setMaxY(this.startPos.getY() + (int)this.yRadius + 4);
      ((BoundingBoxAccessor)this.boundingBox).setMinZ(this.startPos.getZ() - (int)this.zRadius - 4);
      ((BoundingBoxAccessor)this.boundingBox).setMaxZ(this.startPos.getZ() + (int)this.zRadius + 4);
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
      int minX = Mth.floor(caveStartX - this.xRadius) - chunkPos.x * 16 - 1;
      int maxX = Mth.floor(caveStartX + this.xRadius) - chunkPos.x * 16 + 1;
      int minY = Mth.clamp(Mth.floor(caveStartY - this.yRadius) - 1, world.getMinBuildHeight(), world.getMaxBuildHeight());
      int maxY = Mth.clamp(Mth.floor(caveStartY + this.yRadius) + 1, world.getMinBuildHeight(), world.getMaxBuildHeight());
      int minZ = Mth.floor(caveStartZ - this.zRadius) - chunkPos.z * 16 - 1;
      int maxZ = Mth.floor(caveStartZ + this.zRadius) - chunkPos.z * 16 + 1;
      minX = Mth.clamp(minX, 0, 15);
      maxX = Mth.clamp(maxX, 0, 15);
      minZ = Mth.clamp(minZ, 0, 15);
      maxZ = Mth.clamp(maxZ, 0, 15);

      for (float x = minX; x <= maxX; x++) {
         int globalX = (int)x + chunkPos.x * 16;
         if (globalX >= chunkPos.getMinBlockX() && globalX <= chunkPos.getMaxBlockX()) {
            float radialXDist = (globalX - caveStartX + 0.5F) / this.xRadius;

            for (float z = minZ; z <= maxZ; z++) {
               int globalZ = (int)z + chunkPos.z * 16;
               if (globalZ >= chunkPos.getMinBlockZ() && globalZ <= chunkPos.getMaxBlockZ()) {
                  float radialZDist = (globalZ - caveStartZ + 0.5F) / this.zRadius;

                  for (float y = minY; y <= maxY; y++) {
                     int globalY = (int)y;
                     if (globalY > surface[(int)x % 16 * 16 + (int)z % 16]) {
                        break;
                     }

                     float radialYDist = (y - caveStartY - 0.5F) / this.yRadius;
                     int mask = (int)x | (int)z << 4 | (int)(y - world.getMinBuildHeight()) << 8;
                     float radialDist = radialXDist * radialXDist + radialYDist * radialYDist + radialZDist * radialZDist;
                     if (radialDist < 1.0) {
                        if (!carvingMask.get(mask) && !BLOCK_BLACKLIST.contains(this.getBlock(world, globalX, globalY, globalZ, box).getBlock())) {
                           this.placeBlock(world, Blocks.CAVE_AIR.defaultBlockState(), globalX, globalY, globalZ, box);
                           carvingMask.set(mask);
                        }
                     } else {
                        float radialXDistShell = (globalX - caveStartX + 0.5F) / (this.xRadius + 1.2F);
                        float radialYDistShell = (y - caveStartY - 0.5F) / (this.yRadius + 1.2F);
                        float radialZDistShell = (globalZ - caveStartZ + 0.5F) / (this.zRadius + 1.2F);
                        float radialDistShell = radialXDistShell * radialXDistShell + radialYDistShell * radialYDistShell + radialZDistShell * radialZDistShell;
                        if (radialDistShell < 1.0 && !carvingMask.get(mask)) {
                           BlockState state = this.getBlock(world, globalX, globalY, globalZ, box);
                           if (!BLOCK_BLACKLIST.contains(state.getBlock())
                              && state.isAir()
                              && (state.isAir() || state.getFluidState().getType() != Fluids.EMPTY || decoRand.nextFloat() < 0.8F)) {
                              this.placeBlock(world, Blocks.COBBLESTONE.defaultBlockState(), globalX, globalY, globalZ, box);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      BlockPos chestPos = new BlockPos((int)caveStartX, (int)caveStartY - (int)this.yRadius + 1, (int)caveStartZ);
      this.placeSphereRandomized(world, box, chestPos, 2.0F, decoRand, 0.5F, WOOL_SELECTOR, false);
      this.placeBlock(world, Blocks.WHITE_WOOL.defaultBlockState(), chestPos.getX() + 1, chestPos.getY(), chestPos.getZ(), box);
      this.placeBlock(world, Blocks.WHITE_WOOL.defaultBlockState(), chestPos.getX() - 1, chestPos.getY(), chestPos.getZ(), box);
      this.placeBlock(world, Blocks.WHITE_WOOL.defaultBlockState(), chestPos.getX(), chestPos.getY(), chestPos.getZ() + 1, box);
      this.placeBlock(world, Blocks.WHITE_WOOL.defaultBlockState(), chestPos.getX(), chestPos.getY(), chestPos.getZ() - 1, box);
      this.placeBlock(world, Blocks.WHITE_WOOL.defaultBlockState(), chestPos.getX(), chestPos.getY() - 1, chestPos.getZ(), box);
      this.placeBlock(world, Blocks.WHITE_WOOL.defaultBlockState(), chestPos.getX(), chestPos.getY() + 1, chestPos.getZ(), box);
      this.placeSphereRandomized(world, box, chestPos.getX(), chestPos.getY(), chestPos.getZ(), 2.0F, decoRand, 0.4F, COBWEB_SELECTOR, true);
      if (randomSource.nextFloat() < 0.6F) {
         this.createChest(
            world,
            box,
            randomSource,
            chestPos.getX(),
            chestPos.getY(),
            chestPos.getZ(),
            ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("betterdungeons", "spider_dungeon/chests/egg_room"))
         );
      } else if (box.isInside(chestPos)) {
         this.placeBlock(world, Blocks.SPAWNER.defaultBlockState(), chestPos.getX(), chestPos.getY(), chestPos.getZ(), box);
         BlockEntity spawnerBlockEntity = world.getBlockEntity(chestPos);
         if (spawnerBlockEntity instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)spawnerBlockEntity).setEntityId(EntityType.SPIDER, randomSource);
         } else {
            BetterDungeonsCommon.LOGGER.warn("Expected spider spawner entity at {}, but found none!", chestPos);
         }
      }

      this.decorateCave(world, decoRand, chunkPos, box, carvingMask);
   }
}
