package dev.architectury.hooks.fluid.forge;

import dev.architectury.mixin.forge.neoforge.BucketItemAccessor;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

public class FluidBucketHooksImpl {
   public static Fluid getFluid(BucketItem item) {
      return ((BucketItemAccessor)item).getContent();
   }
}
