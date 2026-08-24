package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.WellConfig;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class WellFeature extends Feature<WellConfig> {
   public static final WellFeature FEATURE = new WellFeature();

   public WellFeature() {
      super(WellConfig.CODEC);
   }

   public boolean place(FeaturePlaceContext<WellConfig> context) {
      WorldGenLevel level = context.level();
      BlockPos origin = context.origin();
      WellConfig config = (WellConfig)context.config();
      RandomSource random = context.random();

      for (int x = -2; x <= 2; x++) {
         for (int z = -2; z <= 2; z++) {
            if (level.isEmptyBlock(origin.offset(x, -1, z)) && level.isEmptyBlock(origin.offset(x, -2, z))) {
               return false;
            }
         }
      }

      for (int var16 = -2; var16 <= 2; var16++) {
         for (int y = -3; y <= 3; y++) {
            for (int zx = -2; zx <= 2; zx++) {
               BlockPos pos = origin.offset(var16, y, zx);
               boolean outer = Math.abs(var16) == 2 || Math.abs(zx) == 2;
               boolean middle = Math.abs(var16) == 1 && Math.abs(zx) == 1;
               boolean inner = var16 == 0 && zx == 0;
               boolean axisAligned = var16 == 0 || zx == 0;
               BlockStateProvider blockProvider;
               if (y == -3) {
                  blockProvider = config.standardProvider();
               } else if (y < 0) {
                  if (axisAligned && !outer) {
                     blockProvider = y == -2 ? config.groundProvider() : config.fluidProvider();
                  } else {
                     blockProvider = config.standardProvider();
                  }
               } else if (outer) {
                  blockProvider = (BlockStateProvider)(y > 0
                     ? BlockStateProvider.simple(Blocks.AIR)
                     : (axisAligned ? config.slabProvider() : config.standardProvider()));
               } else if (middle && y != 3) {
                  blockProvider = config.standardProvider();
               } else if (y == 3) {
                  blockProvider = inner ? config.standardProvider() : config.slabProvider();
               } else {
                  blockProvider = BlockStateProvider.simple(Blocks.AIR);
               }

               level.setBlock(pos, blockProvider.getState(random, pos), 2);
            }
         }
      }

      for (int i = 0; i < config.suspiciousPlacements().sample(random); i++) {
         for (int offset = 0; offset < 2; offset++) {
            BlockPos pos = origin.below(offset + 2).relative(Plane.HORIZONTAL.getRandomDirection(random));
            level.setBlock(pos, config.suspiciousProvider().getState(random, pos), 2);
            Optional<BrushableBlockEntity> susBlock = level.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK);
            if (susBlock.isPresent()) {
               susBlock.get().setLootTable(config.suspiciousLootTable(), pos.asLong());
            }
         }
      }

      return true;
   }
}
