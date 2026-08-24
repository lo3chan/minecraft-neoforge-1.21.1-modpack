package net.bobophones.bobolib.entity.layer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlowLayer<T extends Mob, R extends EntityModel<T>> extends EyesLayer<T, R> {
   ResourceLocation texture;

   public GlowLayer(RenderLayerParent<T, R> renderer, ResourceLocation texture) {
      super(renderer);
      this.texture = texture;
   }

   public RenderType renderType() {
      return RenderType.eyes(this.texture);
   }
}
