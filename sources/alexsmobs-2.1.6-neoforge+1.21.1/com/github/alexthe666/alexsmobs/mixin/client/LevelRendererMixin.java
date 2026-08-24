package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.citadel.client.event.EventGetOutlineColor;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({LevelRenderer.class})
public class LevelRendererMixin {
   @Redirect(
      method = {"renderLevel"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"
      )
   )
   private int alexsmobs_getOutlineColor(Entity entity) {
      EventGetOutlineColor event = new EventGetOutlineColor(entity, entity.getTeamColor());
      event.post();
      int color = entity.getTeamColor();
      if (event.isHandled()) {
         color = event.getColor();
      }

      return color;
   }
}
