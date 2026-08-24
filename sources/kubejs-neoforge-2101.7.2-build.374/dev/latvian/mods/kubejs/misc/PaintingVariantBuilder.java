package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

@ReturnsSelf
public class PaintingVariantBuilder extends BuilderBase<PaintingVariant> {
   public transient int width = 1;
   public transient int height = 1;
   public transient ResourceLocation assetId;

   public PaintingVariantBuilder(ResourceLocation id) {
      super(id);
      this.assetId = id;
   }

   public PaintingVariant createObject() {
      return new PaintingVariant(this.width, this.height, this.assetId);
   }

   public PaintingVariantBuilder size(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
   }

   public PaintingVariantBuilder assetId(KubeResourceLocation assetId) {
      this.assetId = assetId.wrapped();
      return this;
   }
}
