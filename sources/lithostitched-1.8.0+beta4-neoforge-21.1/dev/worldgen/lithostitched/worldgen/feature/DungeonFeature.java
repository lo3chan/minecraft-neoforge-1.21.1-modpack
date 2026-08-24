package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.worldgen.feature.config.DungeonConfig;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

public class DungeonFeature extends Feature<DungeonConfig> {
   public static final DungeonFeature FEATURE = new DungeonFeature();

   public DungeonFeature() {
      super(DungeonConfig.CODEC);
   }

   public boolean place(FeaturePlaceContext<DungeonConfig> context) {
      BlockPos startPos = context.origin();
      RandomSource random = context.random();
      WorldGenLevel level = context.level();
      DungeonConfig config = (DungeonConfig)context.config();
      Predicate<BlockState> predicate = config.dungeonInvalidBlocks()
         .map(set -> state -> state.is(set))
         .orElse(state -> state.is(BlockTags.FEATURES_CANNOT_REPLACE))
         .negate();
      int xRadius = config.radius().sample(random);
      int minX = -xRadius - 1;
      int maxX = xRadius + 1;
      int zRadius = config.radius().sample(random);
      int minZ = -zRadius - 1;
      int maxZ = zRadius + 1;
      int openings = 0;

      for (int x = minX; x <= maxX; x++) {
         for (int y = -1; y <= 4; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockPos currentPos = startPos.offset(x, y, z);
               boolean bl = level.getBlockState(currentPos).isSolid();
               if (y == -1 && !bl) {
                  return false;
               }

               if (y == 4 && !bl) {
                  return false;
               }

               if ((x == minX || x == maxX || z == minZ || z == maxZ) && y == 0 && level.isEmptyBlock(currentPos) && level.isEmptyBlock(currentPos.above())) {
                  openings++;
               }
            }
         }
      }

      if (openings >= config.minOpenings() && openings <= config.maxOpenings()) {
         for (int var24 = minX; var24 <= maxX; var24++) {
            for (int y = 3; y >= -1; y--) {
               for (int z = minZ; z <= maxZ; z++) {
                  BlockPos currentPosx = startPos.offset(var24, y, z);
                  BlockState currentState = level.getBlockState(currentPosx);
                  if (var24 == minX || y == -1 || z == minZ || var24 == maxX || y == 4 || z == maxZ) {
                     if (currentPosx.getY() >= context.chunkGenerator().getMinY() && !level.getBlockState(currentPosx.below()).isSolid()) {
                        level.setBlock(currentPosx, Blocks.CAVE_AIR.defaultBlockState(), 2);
                     } else if (currentState.isSolid() && !currentState.is(Blocks.CHEST)) {
                        this.safeSetBlock(
                           level,
                           currentPosx,
                           y == -1 ? config.floorProvider().getState(random, currentPosx) : config.wallProvider().getState(random, currentPosx),
                           predicate
                        );
                     }
                  } else if (!currentState.is(Blocks.CHEST) && !currentState.is(Blocks.SPAWNER)) {
                     this.safeSetBlock(level, currentPosx, Blocks.CAVE_AIR.defaultBlockState(), predicate);
                  }
               }
            }
         }

         for (int var25 = 0; var25 < config.maxChests(); var25++) {
            for (int y = 0; y < 3; y++) {
               int zx = startPos.getX() + random.nextInt(xRadius * 2 + 1) - xRadius;
               int v = startPos.getY();
               int w = startPos.getZ() + random.nextInt(zRadius * 2 + 1) - zRadius;
               BlockPos chestPos = new BlockPos(zx, v, w);
               if (level.isEmptyBlock(chestPos)) {
                  int solidFaces = 0;

                  for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
                     if (level.getBlockState(chestPos.relative(direction)).isSolid()) {
                        solidFaces++;
                     }
                  }

                  if (solidFaces == 1) {
                     this.safeSetBlock(level, chestPos, StructurePiece.reorient(level, chestPos, Blocks.CHEST.defaultBlockState()), predicate);
                     Optional<ChestBlockEntity> chestEntity = level.getBlockEntity(chestPos, BlockEntityType.CHEST);
                     chestEntity.ifPresent(chestBlockEntity -> chestBlockEntity.setLootTable(config.lootTable(), random.nextLong()));
                     break;
                  }
               }
            }
         }

         this.safeSetBlock(level, startPos, Blocks.SPAWNER.defaultBlockState(), predicate);
         if (level.getBlockEntity(startPos) instanceof SpawnerBlockEntity spawner) {
            spawner.setEntityId(config.spawnerMobs().getRandom(random).orElse(EntityType.PIG), random);
         } else {
            Lithostitched.LOGGER.error("Failed to get spawner block entity for dungeon at block position ({})", startPos);
         }

         return true;
      } else {
         return false;
      }
   }
}
