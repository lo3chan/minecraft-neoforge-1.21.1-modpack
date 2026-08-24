package com.yungnickyoung.minecraft.yungsbridges.world.placement;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsbridges.module.PlacementModifierTypeModule;
import java.util.stream.Stream;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

@MethodsReturnNonnullByDefault
public class BridgePlacement extends PlacementModifier {
   public static final MapCodec<BridgePlacement> CODEC = RecordCodecBuilder.mapCodec(
      codec -> codec.group(
            Codec.INT.fieldOf("length").forGetter(bridgePlacement -> bridgePlacement.length),
            Codec.INT.fieldOf("width").forGetter(bridgePlacement -> bridgePlacement.width),
            Codec.INT.fieldOf("min_water_z").forGetter(bridgePlacement -> bridgePlacement.minWaterZ),
            Codec.INT.fieldOf("max_water_z").forGetter(bridgePlacement -> bridgePlacement.maxWaterZ),
            Codec.INT.fieldOf("width_offset").forGetter(bridgePlacement -> bridgePlacement.widthOffset),
            Codec.INT.fieldOf("num_solid_blocks_needed").forGetter(bridgePlacement -> bridgePlacement.numSolidBlocksNeeded),
            Codec.BOOL.optionalFieldOf("is_z_axis", true).forGetter(bridgePlacement -> bridgePlacement.isZAxis)
         )
         .apply(codec, BridgePlacement::new)
   );
   public final int length;
   public final int width;
   public final int minWaterZ;
   public final int maxWaterZ;
   public int widthOffset;
   public int numSolidBlocksNeeded;
   public boolean isZAxis;

   private BridgePlacement(int length, int width, int minWaterZ, int maxWaterZ, int widthOffset, int numSolidBlocksNeeded, boolean isZAxis) {
      this.length = length;
      this.width = width;
      this.minWaterZ = minWaterZ;
      this.maxWaterZ = maxWaterZ;
      this.widthOffset = widthOffset;
      this.numSolidBlocksNeeded = numSolidBlocksNeeded;
      this.isZAxis = isZAxis;
   }

   public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos) {
      MutableBlockPos seaLevelMutable = blockPos.mutable();
      int seaLevel = placementContext.getLevel().getSeaLevel() - 1;
      seaLevelMutable.setY(seaLevel);

      for (int candidateMiddleMinorAxisOffset = this.width / 2 + this.widthOffset + 1;
         candidateMiddleMinorAxisOffset < 16 - this.width / 2 - this.widthOffset;
         candidateMiddleMinorAxisOffset++
      ) {
         for (int candidateStartMajorAxisOffset = 0; candidateStartMajorAxisOffset < 16; candidateStartMajorAxisOffset++) {
            BlockPos startingPos = this.isZAxis
               ? new BlockPos(blockPos.getX() + candidateMiddleMinorAxisOffset, seaLevel, blockPos.getZ() + candidateStartMajorAxisOffset)
               : new BlockPos(blockPos.getX() + candidateStartMajorAxisOffset, seaLevel, blockPos.getZ() + candidateMiddleMinorAxisOffset);
            BlockPos endingPos = this.isZAxis
               ? new BlockPos(blockPos.getX() + candidateMiddleMinorAxisOffset, seaLevel, blockPos.getZ() + candidateStartMajorAxisOffset + this.length + 1)
               : new BlockPos(blockPos.getX() + candidateStartMajorAxisOffset + this.length + 1, seaLevel, blockPos.getZ() + candidateMiddleMinorAxisOffset);
            if (placementContext.getBlockState(startingPos).canOcclude()
               && placementContext.getBlockState(endingPos).canOcclude()
               && placementContext.getHeight(Types.WORLD_SURFACE, startingPos.getX(), startingPos.getZ()) <= seaLevel + 1
               && placementContext.getHeight(Types.WORLD_SURFACE, endingPos.getX(), endingPos.getZ()) <= seaLevel + 1) {
               int numSolidBlocks = 1;

               for (int direction : Lists.newArrayList(new Integer[]{-1, 1})) {
                  for (int minorAxisSolidDist = 1; minorAxisSolidDist <= this.width / 2; minorAxisSolidDist++) {
                     int minorAxisSolidOffset = direction * minorAxisSolidDist;
                     if (this.isZAxis) {
                        seaLevelMutable.set(startingPos.getX() + minorAxisSolidOffset, startingPos.getY(), startingPos.getZ());
                     } else {
                        seaLevelMutable.set(startingPos.getX(), startingPos.getY(), startingPos.getZ() + minorAxisSolidOffset);
                     }

                     if (!placementContext.getBlockState(seaLevelMutable).canOcclude()
                        || placementContext.getHeight(Types.WORLD_SURFACE, seaLevelMutable.getX(), seaLevelMutable.getZ()) > seaLevel + 1) {
                        break;
                     }

                     numSolidBlocks++;
                  }
               }

               if (numSolidBlocks >= this.numSolidBlocksNeeded) {
                  numSolidBlocks = 1;

                  for (int direction : Lists.newArrayList(new Integer[]{-1, 1})) {
                     for (int minorAxisSolidDist = 1; minorAxisSolidDist <= this.width / 2; minorAxisSolidDist++) {
                        int minorAxisSolidOffsetx = direction * minorAxisSolidDist;
                        if (this.isZAxis) {
                           seaLevelMutable.set(endingPos.getX() + minorAxisSolidOffsetx, endingPos.getY(), endingPos.getZ());
                        } else {
                           seaLevelMutable.set(endingPos.getX(), endingPos.getY(), endingPos.getZ() + minorAxisSolidOffsetx);
                        }

                        if (!placementContext.getBlockState(seaLevelMutable).canOcclude()
                           || placementContext.getHeight(Types.WORLD_SURFACE, seaLevelMutable.getX(), seaLevelMutable.getZ()) > seaLevel + 1) {
                           break;
                        }

                        numSolidBlocks++;
                     }
                  }

                  if (numSolidBlocks >= this.numSolidBlocksNeeded) {
                     boolean isAllWater = true;

                     for (int minorAxisWaterOffset = -this.width / 2; minorAxisWaterOffset <= this.width / 2; minorAxisWaterOffset++) {
                        for (int majorAxisWaterOffset = this.minWaterZ; majorAxisWaterOffset <= this.maxWaterZ; majorAxisWaterOffset++) {
                           if (this.isZAxis) {
                              seaLevelMutable.set(startingPos.getX() + minorAxisWaterOffset, seaLevel, startingPos.getZ() + majorAxisWaterOffset);
                           } else {
                              seaLevelMutable.set(startingPos.getX() + majorAxisWaterOffset, seaLevel, startingPos.getZ() + minorAxisWaterOffset);
                           }

                           if (!placementContext.getBlockState(seaLevelMutable).liquid()) {
                              isAllWater = false;
                              break;
                           }
                        }
                     }

                     if (isAllWater) {
                        return this.isZAxis
                           ? Stream.of(new BlockPos(startingPos.getX() - this.width / 2 - this.widthOffset, seaLevel, startingPos.getZ() + 1))
                           : Stream.of(new BlockPos(startingPos.getX() + 1, seaLevel, startingPos.getZ() + this.width / 2 + this.widthOffset));
                     }
                  }
               }
            }
         }
      }

      return Stream.empty();
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierTypeModule.BRIDGE_PLACEMENT;
   }
}
