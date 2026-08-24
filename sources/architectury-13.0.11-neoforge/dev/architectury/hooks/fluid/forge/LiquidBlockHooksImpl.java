package dev.architectury.hooks.fluid.forge;

import dev.architectury.mixin.forge.neoforge.LiquidBlockAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class LiquidBlockHooksImpl {
   public static FlowingFluid getFluid(LiquidBlock block) {
      return ((LiquidBlockAccessor)block).getFluid();
   }
}
