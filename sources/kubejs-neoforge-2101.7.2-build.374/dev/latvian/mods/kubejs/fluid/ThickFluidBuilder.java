package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;

public class ThickFluidBuilder extends FluidBuilder {
   public ThickFluidBuilder(ResourceLocation i) {
      super(i);
      this.fluidType.tint(WATER_COLOR);
      this.fluidType.stillTexture(KubeJS.id("block/thick_fluid_still"));
      this.fluidType.flowingTexture(KubeJS.id("block/thick_fluid_flow"));
      this.fluidType.renderType(BlockRenderType.SOLID);
      this.fluidType.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA);
      this.fluidType.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
      this.fluidType.canSwim(false);
      this.fluidType.canDrown(false);
      this.fluidType.density(3000);
      this.fluidType.viscosity(6000);
      this.slopeFindDistance(2);
      this.tickRate(20);
   }
}
