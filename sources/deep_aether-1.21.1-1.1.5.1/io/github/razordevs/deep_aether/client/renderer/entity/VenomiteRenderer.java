package io.github.razordevs.deep_aether.client.renderer.entity;

import io.github.razordevs.deep_aether.client.model.VenomiteModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.Venomite;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class VenomiteRenderer extends MobRenderer<Venomite, VenomiteModel> {
   public VenomiteRenderer(Context renderManager) {
      super(renderManager, new VenomiteModel(renderManager.bakeLayer(DAModelLayers.VENOMITE)), 0.5F);
   }

   public ResourceLocation getTextureLocation(Venomite instance) {
      return instance.isAngry()
         ? ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/venomite/venomite_angry.png")
         : ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/venomite/venomite.png");
   }
}
