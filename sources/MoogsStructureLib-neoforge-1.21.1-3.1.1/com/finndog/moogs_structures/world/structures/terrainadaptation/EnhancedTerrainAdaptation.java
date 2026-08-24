package com.finndog.moogs_structures.world.structures.terrainadaptation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public class EnhancedTerrainAdaptation {
   public static final EnhancedTerrainAdaptation NONE = new EnhancedTerrainAdaptation(
      0,
      0,
      EnhancedTerrainAdaptation.TerrainAction.NONE,
      EnhancedTerrainAdaptation.TerrainAction.NONE,
      0.0,
      EnhancedTerrainAdaptation.Padding.ZERO,
      Optional.empty()
   );
   public static final Codec<EnhancedTerrainAdaptation> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("kernel_size").forGetter(EnhancedTerrainAdaptation::getKernelSize),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("kernel_distance").forGetter(EnhancedTerrainAdaptation::getKernelDistance),
            EnhancedTerrainAdaptation.TerrainAction.CODEC.fieldOf("top").forGetter(EnhancedTerrainAdaptation::topAction),
            EnhancedTerrainAdaptation.TerrainAction.CODEC.fieldOf("bottom").forGetter(EnhancedTerrainAdaptation::bottomAction),
            Codec.DOUBLE.optionalFieldOf("bottom_offset", 0.0).forGetter(EnhancedTerrainAdaptation::getBottomOffset),
            EnhancedTerrainAdaptation.Padding.CODEC
               .optionalFieldOf("padding", EnhancedTerrainAdaptation.Padding.ZERO)
               .forGetter(EnhancedTerrainAdaptation::getPadding),
            EnhancedTerrainAdaptation.Band.CODEC.optionalFieldOf("band").forGetter(EnhancedTerrainAdaptation::getBand)
         )
         .apply(builder, EnhancedTerrainAdaptation::new)
   );
   private final EnhancedTerrainAdaptation.TerrainAction topAction;
   private final EnhancedTerrainAdaptation.TerrainAction bottomAction;
   private final int kernelSize;
   private final int kernelDistance;
   private final double bottomOffset;
   private final EnhancedTerrainAdaptation.Padding padding;
   private final Optional<EnhancedTerrainAdaptation.Band> band;
   private final float[] kernel;

   public EnhancedTerrainAdaptation(
      int kernelSize,
      int kernelDistance,
      EnhancedTerrainAdaptation.TerrainAction topAction,
      EnhancedTerrainAdaptation.TerrainAction bottomAction,
      double bottomOffset,
      EnhancedTerrainAdaptation.Padding padding,
      Optional<EnhancedTerrainAdaptation.Band> band
   ) {
      this.kernelSize = kernelSize;
      this.kernelDistance = kernelDistance;
      this.topAction = topAction;
      this.bottomAction = bottomAction;
      this.bottomOffset = bottomOffset;
      this.padding = padding;
      this.band = band;
      int kernelRadius = this.getKernelRadius();
      this.kernel = (float[])Util.make(new float[kernelSize * kernelSize * kernelSize], k -> {
         for (int x = 0; x < kernelSize; x++) {
            for (int y = 0; y < kernelSize; y++) {
               for (int z = 0; z < kernelSize; z++) {
                  int i = this.index(x, y, z);
                  double kernelX = x - kernelRadius;
                  double kernelY = y - kernelRadius + 0.5;
                  double kernelZ = z - kernelRadius;
                  k[i] = this.computeKernelValue(kernelX, kernelY, kernelZ);
               }
            }
         }
      });
   }

   private float computeKernelValue(double xDistance, double yDistance, double zDistance) {
      double squaredDistance = Mth.lengthSquared(xDistance, yDistance, zDistance);
      return (float)Math.pow(2.718281828459045, -squaredDistance / this.kernelDistance);
   }

   public EnhancedTerrainAdaptation.TerrainAction topAction() {
      return this.topAction;
   }

   public EnhancedTerrainAdaptation.TerrainAction bottomAction() {
      return this.bottomAction;
   }

   public double getBottomOffset() {
      return this.bottomOffset;
   }

   public EnhancedTerrainAdaptation.Padding getPadding() {
      return this.padding;
   }

   public int getKernelSize() {
      return this.kernelSize;
   }

   public int getKernelRadius() {
      return this.kernelSize / 2;
   }

   public int getKernelDistance() {
      return this.kernelDistance;
   }

   public float[] getKernel() {
      return this.kernel;
   }

   public Optional<EnhancedTerrainAdaptation.Band> getBand() {
      return this.band;
   }

   public double computeDensityFactor(int xDistance, int yDistance, int zDistance, int yDistanceToPieceBottom) {
      int kernelRadius = this.getKernelRadius();
      int kernelX = xDistance + kernelRadius;
      int kernelY = yDistance + kernelRadius;
      int kernelZ = zDistance + kernelRadius;
      if (this.isInKernelRange(kernelX) && this.isInKernelRange(kernelY) && this.isInKernelRange(kernelZ)) {
         int i = this.index(kernelX, kernelY, kernelZ);
         float kernelValue = this.getKernel()[i];
         double actualYDistanceToPieceBottom = yDistanceToPieceBottom + 0.5;
         double squaredDistance = Mth.lengthSquared(xDistance, actualYDistanceToPieceBottom, zDistance);
         double multiplier = Math.abs(actualYDistanceToPieceBottom * Mth.invSqrt(squaredDistance / 2.0) / 2.0);
         boolean isAboveBeardBase = actualYDistanceToPieceBottom > 0.0;
         int densityModifier = isAboveBeardBase ? this.topAction.getDensityModifier() : this.bottomAction.getDensityModifier();
         return multiplier * kernelValue * densityModifier;
      } else {
         return 0.0;
      }
   }

   private boolean isInKernelRange(int i) {
      return i >= 0 && i < this.kernelSize;
   }

   private int index(int x, int y, int z) {
      return z * this.kernelSize * this.kernelSize + x * this.kernelSize + y;
   }

   public record Band(int bottom, int top, Optional<List<Integer>> pieceHeights) {
      public static final Codec<EnhancedTerrainAdaptation.Band> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.INT.fieldOf("bottom").forGetter(EnhancedTerrainAdaptation.Band::bottom),
               Codec.INT.fieldOf("top").forGetter(EnhancedTerrainAdaptation.Band::top),
               Codec.INT.listOf().optionalFieldOf("piece_heights").forGetter(EnhancedTerrainAdaptation.Band::pieceHeights)
            )
            .apply(instance, EnhancedTerrainAdaptation.Band::new)
      );
   }

   public record Padding(int x, int top, int bottom, int z) {
      public static final EnhancedTerrainAdaptation.Padding ZERO = new EnhancedTerrainAdaptation.Padding(0, 0, 0, 0);
      public static final Codec<EnhancedTerrainAdaptation.Padding> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.INT.optionalFieldOf("x", 0).forGetter(padding -> padding.x),
               Codec.INT.optionalFieldOf("top", 0).forGetter(padding -> padding.top),
               Codec.INT.optionalFieldOf("bottom", 0).forGetter(padding -> padding.bottom),
               Codec.INT.optionalFieldOf("z", 0).forGetter(padding -> padding.z)
            )
            .apply(instance, EnhancedTerrainAdaptation.Padding::new)
      );
   }

   public static enum TerrainAction implements StringRepresentable {
      CARVE("carve", -1),
      BURY("bury", 1),
      NONE("none", 0);

      public static final Codec<EnhancedTerrainAdaptation.TerrainAction> CODEC = StringRepresentable.fromValues(EnhancedTerrainAdaptation.TerrainAction::values);
      private final String name;
      private final int densityModifier;

      private TerrainAction(String name, int densityModifier) {
         this.name = name;
         this.densityModifier = densityModifier;
      }

      public int getDensityModifier() {
         return this.densityModifier;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }
   }
}
