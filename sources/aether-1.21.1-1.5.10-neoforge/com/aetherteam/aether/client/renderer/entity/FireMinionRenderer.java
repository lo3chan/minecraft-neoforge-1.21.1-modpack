package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.FireMinionModel;
import com.aetherteam.aether.entity.monster.dungeon.FireMinion;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class FireMinionRenderer extends MobRenderer<FireMinion, FireMinionModel<FireMinion>> {
   private static final ResourceLocation SUN_SPIRIT_TEXTURE = ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/sun_spirit/sun_spirit.png");
   private static final ResourceLocation FROZEN_SPIRIT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/mobs/sun_spirit/frozen_sun_spirit.png"
   );

   public FireMinionRenderer(Context context) {
      super(context, new FireMinionModel(context.bakeLayer(AetherModelLayers.FIRE_MINION)), 0.8F);
   }

   protected void scale(FireMinion fireMinion, PoseStack poseStack, float partialTickTime) {
      poseStack.translate(0.0, 0.35, 0.0);
   }

   public ResourceLocation getTextureLocation(FireMinion fireMinion) {
      if (fireMinion.hasCustomName()) {
         String name = fireMinion.getName().getString();
         if (name.equals("JorgeQ") || name.equals("Jorge_SunSpirit")) {
            return FROZEN_SPIRIT_TEXTURE;
         }
      }

      return SUN_SPIRIT_TEXTURE;
   }

   protected int getBlockLightLevel(FireMinion fireMinion, BlockPos pos) {
      return 15;
   }

   protected int getSkyLightLevel(FireMinion fireMinion, BlockPos pos) {
      return 15;
   }
}
