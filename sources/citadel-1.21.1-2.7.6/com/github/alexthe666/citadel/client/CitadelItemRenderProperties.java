package com.github.alexthe666.citadel.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CitadelItemRenderProperties implements IClientItemExtensions {
   private final BlockEntityWithoutLevelRenderer renderer = new CitadelItemstackRenderer();

   public BlockEntityWithoutLevelRenderer getCustomRenderer() {
      return this.renderer;
   }
}
