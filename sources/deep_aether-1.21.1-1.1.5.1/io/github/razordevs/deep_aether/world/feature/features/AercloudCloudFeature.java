package io.github.razordevs.deep_aether.world.feature.features;

import com.mojang.serialization.Codec;
import io.github.razordevs.deep_aether.datagen.tags.DATags;
import io.github.razordevs.deep_aether.init.DABlocks;
import io.github.razordevs.deep_aether.world.feature.features.configuration.AercloudCloudConfiguration;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class AercloudCloudFeature extends Feature<AercloudCloudConfiguration> {
   public static final PerlinSimplexNoise NOISE = new PerlinSimplexNoise(new XoroshiroRandomSource(42L), List.of(0, 1, 0, 0, 0, 1, 0, 1));
   public static final int lowestY = 155;

   public AercloudCloudFeature(Codec<AercloudCloudConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<AercloudCloudConfiguration> context) {
      WorldGenLevel reader = context.level();
      BlockPos pos = context.origin();
      AercloudCloudConfiguration config = (AercloudCloudConfiguration)context.config();
      this.place(reader, pos, config, context.random(), config.hasGrass());
      return true;
   }

   public void place(WorldGenLevel reader, BlockPos pos, AercloudCloudConfiguration config, RandomSource random, boolean hasGrass) {
      boolean goAgainstX = !reader.getBiomeManager().getNoiseBiomeAtPosition(pos.relative(Axis.X, 16)).is(DATags.Biomes.IS_CLOUD);
      boolean goAgainstNegativeX = !reader.getBiomeManager().getNoiseBiomeAtPosition(pos.relative(Axis.X, -16)).is(DATags.Biomes.IS_CLOUD);
      boolean goAgainstZ = !reader.getBiomeManager().getNoiseBiomeAtPosition(pos.relative(Axis.Z, 16)).is(DATags.Biomes.IS_CLOUD);
      boolean goAgainstNegativeZ = !reader.getBiomeManager().getNoiseBiomeAtPosition(pos.relative(Axis.Z, -16)).is(DATags.Biomes.IS_CLOUD);
      boolean goAgainstXAndZ = !reader.getBiomeManager().getNoiseBiomeAtPosition(pos.relative(Axis.X, 16).relative(Axis.Z, 16)).is(DATags.Biomes.IS_CLOUD)
         && !goAgainstX
         && !goAgainstZ;
      boolean goAgainstXAndNegativeZ = !reader.getBiomeManager()
            .getNoiseBiomeAtPosition(pos.relative(Axis.X, 16).relative(Axis.Z, -16))
            .is(DATags.Biomes.IS_CLOUD)
         && !goAgainstX
         && !goAgainstNegativeZ;
      boolean goAgainstNegativeXAndZ = !reader.getBiomeManager()
            .getNoiseBiomeAtPosition(pos.relative(Axis.X, -16).relative(Axis.Z, 16))
            .is(DATags.Biomes.IS_CLOUD)
         && !goAgainstNegativeX
         && !goAgainstZ;
      boolean goAgainstNegativeXAndNegativeZ = !reader.getBiomeManager()
            .getNoiseBiomeAtPosition(pos.relative(Axis.X, -16).relative(Axis.Z, -16))
            .is(DATags.Biomes.IS_CLOUD)
         && !goAgainstNegativeX
         && !goAgainstNegativeZ;
      int chunkX = pos.getX() - pos.getX() % 16;
      int chunkZ = pos.getZ() - pos.getZ() % 16;

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            int xCoord = chunkX + x;
            int zCoord = chunkZ + z;
            double bottomNoiseValue = NOISE.getValue(xCoord * 0.02, zCoord * 0.02, false);
            double bottom = Math.abs(Mth.lerp(bottomNoiseValue, 4.0, 2.0));
            double originalBottom = bottom;
            double topNoiseValue = NOISE.getValue(xCoord * 0.007, zCoord * 0.007, false);
            double top = Mth.lerp(topNoiseValue, -2.0, 7.0) + 2.0;
            if (top < 3.0) {
               top -= (3.0 - top) * 2.0;
            }

            double decreaseMultiplier = 1.5;
            double decreaseMultiplierCorner = 4.0;
            if (goAgainstX && x > 8) {
               bottom += (x - 8.0) / 1.5;
               top -= (x - 8.0) / 1.5;
            }

            if (goAgainstNegativeX && x <= 8) {
               bottom += (8.0 - x) / 1.5;
               top -= (8.0 - x) / 1.5;
            }

            if (goAgainstZ && z > 8) {
               bottom += (z - 8.0) / 1.5;
               top -= (z - 8.0) / 1.5;
            }

            if (goAgainstNegativeZ) {
               if (z < 8) {
                  bottom += (8.0 - z) / 1.5;
                  top -= (8.0 - z) / 1.5;
               }
            } else if (goAgainstXAndZ) {
               if (x > 12) {
                  bottom += (x - 12.0) / 4.0;
                  top -= (x - 12.0) / 4.0;
               }

               if (z > 12) {
                  bottom += (z - 12.0) / 4.0;
                  top -= (z - 12.0) / 4.0;
               }
            } else if (goAgainstXAndNegativeZ) {
               if (x > 12) {
                  bottom += (x - 12.0) / 4.0;
                  top -= (x - 12.0) / 4.0;
               }

               if (z < 4) {
                  bottom += (4.0 - z) / 4.0;
                  top -= (4.0 - z) / 4.0;
               }
            } else if (goAgainstNegativeXAndZ) {
               if (x < 4) {
                  bottom += (4.0 - x) / 4.0;
                  top -= (4.0 - x) / 4.0;
               }

               if (z > 12) {
                  bottom += (z - 12.0) / 4.0;
                  top -= (z - 12.0) / 4.0;
               }
            } else if (goAgainstNegativeXAndNegativeZ) {
               if (x < 4) {
                  bottom += (4.0 - x) / 4.0;
                  top -= (4.0 - x) / 4.0;
               }

               if (z < 4) {
                  bottom += (4.0 - z) / 4.0;
                  top -= (4.0 - z) / 4.0;
               }
            }

            int y;
            for (y = Math.round((float)Math.round(155.0 + bottom - top / 2.0)); y < 155.0 + top + originalBottom; y++) {
               this.setBlock(reader, pos.relative(Axis.X, x).relative(Axis.Z, z).atY(y), config.block().getState(random, pos));
            }

            if (hasGrass && top >= 4.0) {
               this.setBlock(reader, pos.relative(Axis.X, x).relative(Axis.Z, z).atY(y), ((Block)DABlocks.AERCLOUD_GRASS_BLOCK.get()).defaultBlockState());
            }

            originalBottom *= 4.0;
            int a = 0;
            if (originalBottom < 12.0) {
               a = (int)(12.0 - originalBottom);
            }

            for (int var37 = 12 + a; var37 > originalBottom; var37--) {
               this.setBlock(reader, pos.relative(Axis.X, x).relative(Axis.Z, z).atY(var37 + 155 + 20), config.block().getState(random, pos));
            }
         }
      }
   }
}
