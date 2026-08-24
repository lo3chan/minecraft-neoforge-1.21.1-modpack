package io.github.razordevs.deep_aether.client.renderer.entity;

import io.github.razordevs.deep_aether.client.model.WindflyModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.Windfly;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WindflyRenderer extends MobRenderer<Windfly, WindflyModel> {
   public WindflyRenderer(Context renderManager) {
      super(renderManager, new WindflyModel(renderManager.bakeLayer(DAModelLayers.WINDFLY)), 0.5F);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull Windfly instance) {
      return ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/windfly/windfly.png");
   }
}
