package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import net.minecraft.resources.ResourceLocation;

public class ThinFluidBuilder extends FluidBuilder {
   public ThinFluidBuilder(ResourceLocation i) {
      super(i);
      this.fluidType.tint(WATER_COLOR);
      this.fluidType.stillTexture(KubeJS.id("block/thin_fluid_still"));
      this.fluidType.flowingTexture(KubeJS.id("block/thin_fluid_flow"));
      this.fluidType.renderType(BlockRenderType.TRANSLUCENT);
      this.fluidType.fallDistanceModifier(0.0F);
   }
}
