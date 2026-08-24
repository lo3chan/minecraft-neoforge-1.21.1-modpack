package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.PhoenixArrowAttachment;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({TippableArrowRenderer.class})
public class TippableArrowRendererMixin {
   @Unique
   private static final ResourceLocation FLAMING_ARROW_LOCATION = ResourceLocation.fromNamespaceAndPath(
      "aether", "textures/entity/projectile/flaming_arrow.png"
   );

   @ModifyReturnValue(
      at = {@At("RETURN")},
      method = {"getTextureLocation(Lnet/minecraft/world/entity/projectile/Arrow;)Lnet/minecraft/resources/ResourceLocation;"}
   )
   private ResourceLocation getTextureLocation(ResourceLocation original, @Local(ordinal = 0,argsOnly = true) Arrow entity) {
      return entity.hasData(AetherDataAttachments.PHOENIX_ARROW)
            && ((PhoenixArrowAttachment)entity.getData(AetherDataAttachments.PHOENIX_ARROW)).isPhoenixArrow()
            && entity.getColor() <= 0
         ? FLAMING_ARROW_LOCATION
         : original;
   }
}
