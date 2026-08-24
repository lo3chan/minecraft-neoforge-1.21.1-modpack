package com.yungnickyoung.minecraft.betterendisland.mixin;

import com.yungnickyoung.minecraft.betterendisland.world.IEndSpike;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.SpikeFeature.EndSpike;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EndSpike.class})
public abstract class EndSpikeMixin implements IEndSpike {
   @Shadow
   @Final
   @Mutable
   private AABB topBoundingBox;
   @Shadow
   @Final
   private int height;
   @Unique
   private int crystalHeight = 0;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void betterendisland_adjustSpikeBoundingBox(int centerX, int centerZ, int radius, int height, boolean guarded, CallbackInfo ci) {
      this.topBoundingBox = new AABB(centerX - 9, DimensionType.MIN_Y, centerZ - 9, centerX + 9, DimensionType.MAX_Y, centerZ + 9);
   }

   @Inject(
      method = {"getHeight"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void betterendisland_getSpikeHeight(CallbackInfoReturnable<Integer> cir) {
      cir.setReturnValue(this.height);
   }

   @Unique
   @Override
   public int getCrystalYOffset() {
      return this.crystalHeight;
   }

   @Unique
   @Override
   public void setCrystalYOffsetFromPillarHeight(int pillarHeight) {
      this.crystalHeight = switch (pillarHeight) {
         case 1, 2 -> 22;
         case 3 -> 26;
         case 4, 5 -> 27;
         case 6, 7, 8 -> 32;
         default -> 38;
      };
   }
}
