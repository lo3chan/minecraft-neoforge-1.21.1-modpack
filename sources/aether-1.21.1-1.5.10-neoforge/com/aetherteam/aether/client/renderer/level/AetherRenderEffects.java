package com.aetherteam.aether.client.renderer.level;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

public class AetherRenderEffects {
   public static final DimensionSpecialEffects AETHER_EFFECTS = new AetherSkyRenderEffects();

   public static void registerRenderEffects(RegisterDimensionSpecialEffectsEvent event) {
      event.register(AetherDimensions.AETHER_DIMENSION_TYPE.location(), AETHER_EFFECTS);
   }
}
