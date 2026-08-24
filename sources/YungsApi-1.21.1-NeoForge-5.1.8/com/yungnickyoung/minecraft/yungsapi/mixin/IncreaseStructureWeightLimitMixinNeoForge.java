package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({StructureTemplatePool.class})
public class IncreaseStructureWeightLimitMixinNeoForge {
   @WrapOperation(
      method = {"lambda$static$1", "method_28886"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/serialization/Codec;intRange(II)Lcom/mojang/serialization/Codec;"
      )},
      require = 0,
      remap = false
   )
   private static Codec<Integer> yungsapi_increaseWeightLimit(int minRange, int maxRange, Operation<Codec<Integer>> original) {
      return (Codec<Integer>)original.call(new Object[]{minRange, 5000});
   }
}
