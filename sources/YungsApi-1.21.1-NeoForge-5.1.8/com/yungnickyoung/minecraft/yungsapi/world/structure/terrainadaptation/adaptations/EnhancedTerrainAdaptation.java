package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride.AquiferOverride;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public abstract class EnhancedTerrainAdaptation {
   public static final EnhancedTerrainAdaptation NONE = new NoneAdaptation();
   private final EnhancedTerrainAdaptation.TerrainAction topAction;
   private final EnhancedTerrainAdaptation.TerrainAction bottomAction;
   private final int kernelSize;
   private final int kernelDistance;
   private final double bottomOffset;
   private final EnhancedTerrainAdaptation.Padding padding;
   private final AquiferOverride aquiferOverride;
   private final float[] kernel;

   public abstract EnhancedTerrainAdaptationType<?> type();

   EnhancedTerrainAdaptation(
      int kernelSize,
      int kernelDistance,
      EnhancedTerrainAdaptation.TerrainAction topAction,
      EnhancedTerrainAdaptation.TerrainAction bottomAction,
      double bottomOffset,
      EnhancedTerrainAdaptation.Padding padding,
      AquiferOverride aquiferOverride
   ) {
      this.kernelSize = kernelSize;
      this.kernelDistance = kernelDistance;
      this.topAction = topAction;
      this.bottomAction = bottomAction;
      this.bottomOffset = bottomOffset;
      this.padding = padding;
      this.aquiferOverride = aquiferOverride;
      int kernelRadius = this.getKernelRadius();
      this.kernel = (float[])Util.make(new float[kernelSize * kernelSize * kernelSize], kernel -> {
         for (int x = 0; x < kernelSize; x++) {
            for (int y = 0; y < kernelSize; y++) {
               for (int z = 0; z < kernelSize; z++) {
                  int i = this.index(x, y, z);
                  double kernelX = x - kernelRadius;
                  double kernelY = y - kernelRadius + 0.5;
                  double kernelZ = z - kernelRadius;
                  kernel[i] = this.computeKernelValue(kernelX, kernelY, kernelZ);
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

   public AquiferOverride getAquiferOverride() {
      return this.aquiferOverride;
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
