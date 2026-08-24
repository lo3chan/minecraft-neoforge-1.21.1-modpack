package io.github.razordevs.deep_aether.item.misc;

import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketWrapper;
import io.github.razordevs.deep_aether.init.DAFluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class SkyrootPoisonBucketWrapper extends SkyrootBucketWrapper {
   public SkyrootPoisonBucketWrapper(ItemStack container) {
      super(container);
   }

   public FluidStack getFluid() {
      return new FluidStack((Fluid)DAFluids.POISON_FLUID.get(), 1000);
   }
}
