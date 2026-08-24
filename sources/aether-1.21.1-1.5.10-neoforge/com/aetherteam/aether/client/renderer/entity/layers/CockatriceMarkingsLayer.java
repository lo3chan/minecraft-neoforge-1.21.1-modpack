package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.client.renderer.entity.model.BipedBirdModel;
import com.aetherteam.aether.entity.monster.Cockatrice;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CockatriceMarkingsLayer<T extends Cockatrice, M extends BipedBirdModel<T>> extends EyesLayer<T, M> {
   private static final RenderType COCKATRICE_MARKINGS = RenderType.eyes(
      ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/cockatrice/cockatrice_emissive.png")
   );

   public CockatriceMarkingsLayer(RenderLayerParent<T, M> entityRenderer) {
      super(entityRenderer);
   }

   public RenderType renderType() {
      return COCKATRICE_MARKINGS;
   }
}
