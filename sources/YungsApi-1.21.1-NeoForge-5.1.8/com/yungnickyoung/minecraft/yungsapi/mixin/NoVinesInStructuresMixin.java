package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.yungnickyoung.minecraft.yungsapi.module.TagModule;
import com.yungnickyoung.minecraft.yungsapi.util.MixinUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.VinesFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({VinesFeature.class})
public class NoVinesInStructuresMixin {
   @Inject(
      method = {"place"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void yungsapi_noVinesInStructures(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
      if (context.level() instanceof WorldGenRegion worldGenRegion) {
         MutableBlockPos var7 = new MutableBlockPos();

         for (Direction direction : Plane.HORIZONTAL) {
            var7.set(context.origin()).move(direction);
            if (MixinUtils.isPositionInTaggedStructure(worldGenRegion, var7, TagModule.NO_VINES)) {
               cir.setReturnValue(false);
            }
         }
      }
   }
}
