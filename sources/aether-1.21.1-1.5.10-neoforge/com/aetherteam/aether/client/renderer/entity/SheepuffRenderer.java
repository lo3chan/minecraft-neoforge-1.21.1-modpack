package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.SheepuffWoolLayer;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffModel;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffWoolModel;
import com.aetherteam.aether.entity.passive.Sheepuff;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SheepuffRenderer extends MobRenderer<Sheepuff, SheepuffModel> {
   private static final ResourceLocation SHEEPUFF_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/sheepuff/sheepuff.png");

   public SheepuffRenderer(Context context) {
      super(context, new SheepuffModel(context.bakeLayer(AetherModelLayers.SHEEPUFF)), 0.7F);
      this.addLayer(
         new SheepuffWoolLayer(
            this,
            new SheepuffWoolModel(context.bakeLayer(AetherModelLayers.SHEEPUFF_WOOL)),
            new SheepuffWoolModel(context.bakeLayer(AetherModelLayers.SHEEPUFF_WOOL_PUFFED))
         )
      );
   }

   public ResourceLocation getTextureLocation(Sheepuff sheepuff) {
      return SHEEPUFF_TEXTURE;
   }
}
